package sprint.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.datatransferobject.request.ModifyGroupInfoRequest;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.Groups;
import sprint.server.domain.groupmember.GroupMember;
import sprint.server.domain.groupmember.GroupMemberState;
import sprint.server.domain.member.Member;
import sprint.server.repository.GroupMemberRepository;
import sprint.server.repository.GroupRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final MemberService memberService;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    /**
     * 그룹 만들기
     * @param group : Group
     * @return : groups ID
     */
    @Transactional
    public Integer join(Groups group) {
        log.info("Group ID : {}, 그룹 생성 요청", group.getId());
        if (existsByNickname(group.getGroupName())) {
            log.error("동일 이름 그룹이 존재합니다.");
            throw new ApiException(ExceptionEnum.GROUP_NAME_ALREADY_EXISTS);
        }

        Member leader = memberService.findById(group.getGroupLeaderId());
        groupRepository.save(group);
        GroupMember groupMember = new GroupMember(group, leader);
        groupMember.setGroupMemberState(GroupMemberState.LEADER);
        groupMemberRepository.save(groupMember);
        log.info("Group ID : {}, 그룹 만들기 성공", group.getId());
        return group.getId();
    }

    /**
     * 그룹 가입 요청
     * @param group : Group
     * @param member : Member
     * @return result(t/f)
     */
    @Transactional
    public boolean requestJoinGroupMember(Groups group, Member member) {
        log.info("ID : {}, Group ID : {}, 그룹 가입 요청", member.getId(), group.getId());
        GroupMember groupMember = new GroupMember(group, member);
        if (groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember.getGroupMemberId(), GroupMemberState.LEADER) ||
                groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember.getGroupMemberId(), GroupMemberState.ACCEPT)) {
            log.error("이미 가입된 그룹입니다.");
            throw new ApiException(ExceptionEnum.GROUP_ALREADY_JOINED);
        }
        if (groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember.getGroupMemberId(), GroupMemberState.REQUEST)) {
            log.error("이미 전송된 요청입니다.");
            throw new ApiException(ExceptionEnum.GROUP_ALREADY_REQUESTED);
        }
        groupMemberRepository.save(groupMember);

        log.info("ID : {}, Group ID : {}, 그룹 가입 요청 완료", member.getId(), group.getId());
        return groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember.getGroupMemberId(), GroupMemberState.REQUEST);
    }

    /**
     * 그룹 가입 요청 응답
     * @param group : group
     * @param member : member
     * @param groupMemberState (Only for ACCEPT, REJECT, CANCEL)
     * @return result(t/f)
     */
    @Transactional
    public Boolean answerGroupMember(Groups group, Member member, GroupMemberState groupMemberState) {
        log.info("ID : {}, Group ID : {}, 요청 응답: {}, 그룹 가입 응답 요청", member.getId(), group.getId(), groupMemberState);
        GroupMember groupMember = findJoinRequestByGroupAndMember(group, member);
        groupMember.setGroupMemberState(groupMemberState);

        if (groupMemberState.equals(GroupMemberState.ACCEPT)) {
            if(group.getGroupMaxPersonnel() > group.getGroupPersonnel()){
                log.info("그룹 가입 승인 완료");
                group.addMember();
            } else {
                log.error("그룹원 초과로 인한 그룹 가입 승인 실패");
                throw new ApiException(ExceptionEnum.GROUP_PERSONNEL_FULL);
            }
        }
        log.info("ID : {}, Group ID : {}, 요청 응답: {}, 그룹 가입 응답 완료", member.getId(), group.getId(), groupMemberState);
        return groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember.getGroupMemberId(), groupMemberState);
    }

    /**
     * 그룹 탈퇴 요청
     * @param group : group
     * @param member : member
     * @return result(t/f)
     */
    @Transactional
    public Boolean leaveGroupMember(Groups group, Member member) {
        log.info("요청 유저 : {}, 요청 그룹 : {} 그룹 탈퇴 요청", member.getId(), group.getId());
        GroupMember groupMember = findJoinedGroupMemberByGroupAndMember(group, member);
        if (groupMember.getGroupMemberState().equals(GroupMemberState.LEADER)) {
            log.error("그룹 리더는 탈퇴할 수 없습니다.");
            throw new ApiException(ExceptionEnum.GROUP_LEADER_CANT_LEAVE);
        }
        if (!groupMember.getGroupMemberState().equals(GroupMemberState.ACCEPT)) {
            log.error("요청 유저({})가 요청 그룹({})에 존재하지 않습니다.", member.getId(), group.getId());
            throw new ApiException(ExceptionEnum.GROUP_MEMBER_NOT_FOUND);
        }

        groupMember.setGroupMemberState(GroupMemberState.LEAVE);
        group.leaveMember();
        log.info("ID : {}, Group ID : {}, 그룹 탈퇴 완료", member.getId(), group.getId());
        return groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember.getGroupMemberId(), GroupMemberState.LEAVE);
    }

    /**
     * 그룹 삭제
     * @param group : group
     * @return
     */
    @Transactional
    public Boolean deleteGroup(Groups group) {
        log.info("Group ID : {}, 그룹 삭제 요청", group.getId());
        Integer groupId = group.getId();
        List<GroupMember> groupMemberList = groupMemberRepository.findAllMemberByGroupId(groupId);
        List<GroupMember> requestGroupMemberList = groupMemberRepository.findRequestGroupMemberByGroupId(groupId);

        groupMemberList.forEach(groupMember -> groupMember.setGroupMemberState(GroupMemberState.LEAVE));
        requestGroupMemberList.forEach(groupMember -> groupMember.setGroupMemberState(GroupMemberState.REJECT));
        group.delete();
        log.info("Group ID : {}, 그룹 삭제 완료", groupId);
        return group.getIsDeleted().equals(true) && groupMemberRepository.findAllMemberByGroupId(groupId).isEmpty();
    }

    /**
     * 그룹장 변경 요청
     * @param group : group
     * @param newLeader : member
     * @return result(t/f)
     */
    @Transactional
    public Boolean changeGroupLeaderByGroupAndMember(Groups group, Member newLeader) {
        log.info("Group ID : {}, 새로운 그룹장 : {}, 모든 그룹원 위임 요청", group.getId(), newLeader.getId());
        Member existLeader = findGroupLeader(group);
        if (existLeader.equals(newLeader)) {
            throw new ApiException(ExceptionEnum.GROUP_ALREADY_LEADER);
        }
        GroupMember existLeaderMember = findJoinedGroupMemberByGroupAndMember(group, existLeader);
        GroupMember newLeaderMember = findJoinedGroupMemberByGroupAndMember(group, newLeader);
        existLeaderMember.setGroupMemberState(GroupMemberState.ACCEPT);
        newLeaderMember.setGroupMemberState(GroupMemberState.LEADER);
        group.changeGroupLeader(newLeader.getId());
        log.info("Group ID : {}, 새로운 그룹장 : {}, 모든 그룹원 위임 완료", group.getId(), newLeader.getId());
        return findGroupLeader(group).equals(newLeader)
                && existLeaderMember.getGroupMemberState().equals(GroupMemberState.ACCEPT);
    }

    /**
     * 그룹 정보 변경
     * @param group : group
     * @param request : information
     * @return
     */
    @Transactional
    public Boolean modifyGroupInfo(Groups group, ModifyGroupInfoRequest request) {
        log.info("Group ID : {}, 그룹 정보 변경 요청", group.getId());
        group.changeDescriptionAndPicture(request.getGroupDescription(), request.getGroupPicture());
        log.info("Group ID : {}, 그룹 정보 변경 성공", group.getId());
        return group.getGroupDescription().equals(request.getGroupDescription()) &&
                group.getGroupPicture().equals(request.getGroupPicture());
    }

    /**
     * 그룹장 조회
     * @param group : group
     * @return GroupMember
     */
    public Member findGroupLeader(Groups group) {
        log.info("Group ID : {}, 그룹 리더 조회", group.getId());
        Optional<GroupMember> leader = groupMemberRepository.findGroupLeaderByGroupId(group.getId());
        if (leader.isEmpty()) {
            log.error("Group ID : {}, 그룹 리더 조회 실패", group.getId());
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        }
        Member mLeader = memberService.findById(leader.get().getMemberId());
        log.info("요청 그룹 : {}, 그룹 리더 조회 완료 : {}", group.getId(), mLeader.getId());
        return mLeader;
    }

    /**
     * 그룹원 조회 (group, member)
     * @param group : group
     * @param member : member
     * @return GroupMember
     */
    public GroupMember findJoinedGroupMemberByGroupAndMember(Groups group, Member member) {
        Optional<GroupMember> groupMember = groupMemberRepository.findByGroupIdAndMemberId(group.getId(), member.getId());
        if (groupMember.isEmpty() ||
                !(groupMember.get().getGroupMemberState().equals(GroupMemberState.ACCEPT) ||
                groupMember.get().getGroupMemberState().equals(GroupMemberState.LEADER))) {

            throw new ApiException(ExceptionEnum.GROUP_MEMBER_NOT_FOUND);
        }
        return groupMember.get();
    }

    /**
     * 가입한 모든 GroupMember 조회
     * @param member : member
     * @return
     */
    public List<GroupMember> findJoinedGroupMemberByMember(Member member) {
        return groupMemberRepository.findJoinedGroupByMemberId(member.getId());
    }

    /**
     * 그룹 가입 요청 조회
     * @param group : group
     * @param member : member
     * @return
     */
    public GroupMember findJoinRequestByGroupAndMember(Groups group, Member member) {
        Optional<GroupMember> groupMember = groupMemberRepository.findByGroupIdAndMemberId(group.getId(), member.getId());
        if (groupMember.isEmpty() || !groupMember.get().getGroupMemberState().equals(GroupMemberState.REQUEST)) {
            throw new ApiException(ExceptionEnum.GROUP_REQUEST_NOT_FOUND);
        }
        return groupMember.get();
    }

    /**
     * 가입한 모든 Group 조회
     * @param member : member
     * @return
     */
    public List<Groups> findAllJoinedGroupByMember(Member member) {
        List<GroupMember> groupMemberList = findJoinedGroupMemberByMember(member);
        return groupMemberList.stream()
                .map(groupMember->findGroupByGroupId(groupMember.getGroupMemberId().getGroupId()))
                .collect(Collectors.toList());
    }

    /**
     * 요청 보낸 모든 GroupMember 조회
     * @param member : member
     * @return
     */
    public List<Groups> findRequestGroupMemberByMember(Member member) {
        List<GroupMember> groupMemberList = groupMemberRepository.findByMemberIdAndState(member.getId(), GroupMemberState.REQUEST);
        return groupMemberList.stream()
                .map(groupMember -> findGroupByGroupId(groupMember.getGroupId()))
                .collect(Collectors.toList());
    }

    /**
     * 모든 그룹원 조회
     * @param group : group
     * @return
     */
    public List<Member> findAllMemberByGroup(Groups group) {
        log.info("Group ID : {}, 모든 그룹원 정보 요청", group.getId());
        return groupMemberRepository.findAllMemberByGroupId(group.getId())
                .stream()
                .map(gm -> memberService.findById(gm.getMemberId()))
                .collect(Collectors.toList());
    }

    /**
     * Validation Group exists, not delete.
     * @param groupId
     * @return
     */
    public Groups findGroupByGroupId(Integer groupId) {
        log.info("Group ID : {}, 그룹 검색", groupId);
        Optional<Groups> group = groupRepository.findById(groupId);
        if (group.isEmpty()) {
            log.error("Group ID : {}, 그룹 검색 실패", groupId);
            throw new ApiException(ExceptionEnum.GROUP_NOT_FOUND);
        }
        if (group.get().getIsDeleted().equals(true)) {
            log.error("Group ID : {}, 그룹 검색 실패", groupId);
            throw new ApiException(ExceptionEnum.GROUP_DELETED);
        }
        log.info("Group ID : {}, 그룹 검색 완료", groupId);
        return group.get();
    }

    public List<Groups> findGroupByString(String target) {
        return groupRepository.findByGroupNameContaining(target)
                .stream()
                .filter(groups -> groups.getIsDeleted().equals(false))
                .collect(Collectors.toList());
    }

    public boolean existsByNickname(String nickname) {
        if(nickname.equals("default")) return true;
        return groupRepository.existsByGroupName(nickname);
    }
}
