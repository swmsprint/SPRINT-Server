package sprint.server.service;

import lombok.RequiredArgsConstructor;
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
        if (groupRepository.existsByGroupName(group.getGroupName())) throw new ApiException(ExceptionEnum.GROUPS_NAME_ALREADY_EXISTS);
        if (!memberService.existById(group.getGroupLeaderId())) throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        Member leader = memberService.findById(group.getGroupLeaderId());
        groupRepository.save(group);
        GroupMember groupMember = new GroupMember(group, leader);
        groupMember.setGroupMemberState(GroupMemberState.LEADER);
        groupMemberRepository.save(groupMember);
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
        GroupMember groupMember = new GroupMember(group, member);
        if (groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember.getGroupMemberId(), GroupMemberState.LEADER) ||
                groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember.getGroupMemberId(), GroupMemberState.ACCEPT))
            throw new ApiException(ExceptionEnum.GROUPS_ALREADY_JOINED);
        if (groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember.getGroupMemberId(), GroupMemberState.REQUEST)) {
            throw new ApiException(ExceptionEnum.GROUPS_ALREADY_REQUESTED);
        }
        groupMemberRepository.save(groupMember);
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
        GroupMember groupMember = findJoinRequestByGroupAndMember(group, member);
        groupMember.setGroupMemberState(groupMemberState);

        if (groupMemberState.equals(GroupMemberState.ACCEPT)) {
            if(group.getGroupMaxPersonnel() > group.getGroupPersonnel()){
                group.addMember();
            } else {
                throw new ApiException(ExceptionEnum.GROUPS_FULL);
            }
        }
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
        GroupMember groupMember = findJoinedGroupMemberByGroupAndMember(group, member);
        if (groupMember.getGroupMemberState().equals(GroupMemberState.LEADER))
            throw new ApiException(ExceptionEnum.GROUPS_LEADER_CANT_LEAVE);
        if (!groupMember.getGroupMemberState().equals(GroupMemberState.ACCEPT))
            throw new ApiException(ExceptionEnum.GROUPS_MEMBER_NOT_FOUND);

        groupMember.setGroupMemberState(GroupMemberState.LEAVE);
        group.leaveMember();
        return groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember.getGroupMemberId(), GroupMemberState.LEAVE);
    }

    /**
     * 그룹 삭제
     * @param group : group
     * @return
     */
    @Transactional
    public Boolean deleteGroup(Groups group) {
        Integer groupId = group.getId();
        List<GroupMember> groupMemberList = groupMemberRepository.findAllMemberByGroupId(groupId);
        List<GroupMember> requestGroupMemberList = groupMemberRepository.findRequestGroupMemberByGroupId(groupId);

        groupMemberList.forEach(groupMember -> groupMember.setGroupMemberState(GroupMemberState.LEAVE));
        requestGroupMemberList.forEach(groupMember -> groupMember.setGroupMemberState(GroupMemberState.REJECT));
        group.delete();
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
        Member existLeader = findGroupLeader(group);
        if (existLeader.equals(newLeader)) {
            throw new ApiException(ExceptionEnum.GROUPS_ALREADY_LEADER);
        }
        GroupMember existLeaderMember = findJoinedGroupMemberByGroupAndMember(group, existLeader);
        GroupMember newLeaderMember = findJoinedGroupMemberByGroupAndMember(group, newLeader);
        existLeaderMember.setGroupMemberState(GroupMemberState.ACCEPT);
        newLeaderMember.setGroupMemberState(GroupMemberState.LEADER);
        group.changeGroupLeader(newLeader.getId());
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
        group.changeDescriptionAndPicture(request.getGroupDescription(), request.getGroupPicture());
        return group.getGroupDescription().equals(request.getGroupDescription()) &&
                group.getGroupPicture().equals(request.getGroupPicture());
    }

    /**
     * 그룹장 조회
     * @param group : group
     * @return GroupMember
     */
    public Member findGroupLeader(Groups group) {
        Optional<GroupMember> leader = groupMemberRepository.findGroupLeaderByGroupId(group.getId());
        if (leader.isEmpty()) throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        return memberService.findById(leader.get().getMemberId());
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

            throw new ApiException(ExceptionEnum.GROUPS_MEMBER_NOT_FOUND);
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
            throw new ApiException(ExceptionEnum.GROUPS_REQUEST_NOT_FOUND);
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
        Optional<Groups> group = groupRepository.findById(groupId);
        if (group.isEmpty()) throw new ApiException(ExceptionEnum.GROUPS_NOT_FOUND);
        if (group.get().getIsDeleted().equals(true)) throw new ApiException(ExceptionEnum.GROUPS_DELETED);
        return group.get();
    }
}
