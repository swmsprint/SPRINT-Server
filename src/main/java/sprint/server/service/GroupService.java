package sprint.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.datatransferobject.request.ModifyGroupInfoRequest;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.Groups;
import sprint.server.domain.groupmember.GroupMember;
import sprint.server.domain.groupmember.GroupMemberId;
import sprint.server.domain.groupmember.GroupMemberState;
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
     * @param groups : Groups
     * @return : group ID
     */
    @Transactional
    public Integer join(Groups groups) {
        validationBeforeCreateGroup(groups);
        groupRepository.save(groups);
        GroupMember groupMember = new GroupMember(new GroupMemberId(groups.getId(), groups.getGroupLeaderId()));
        groupMember.setGroupMemberState(GroupMemberState.LEADER);
        groupMemberRepository.save(groupMember);
        return groups.getId();
    }

    /**
     * 그룹 가입 요청
     * @param groupMember
     * @return result(t/f)
     */
    @Transactional
    public Boolean requestJoinGroupMember(GroupMember groupMember) {
        validationBeforeJoinGroup(groupMember.getGroupMemberId());
        groupMemberRepository.save(groupMember);
        return groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember.getGroupMemberId(), GroupMemberState.REQUEST);
    }

    /**
     * 그룹 가입 요청 응답
     * @param groupMemberId
     * @param groupMemberState (Only for ACCEPT, REJECT, CANCEL)
     * @return result(t/f)
     */
    @Transactional
    public Boolean answerGroupMember(GroupMemberId groupMemberId, GroupMemberState groupMemberState) {
        validationBeforeAnswerGroupMember(groupMemberId);
        GroupMember groupMember = groupMemberRepository.findById(groupMemberId).get();
        Groups groups = findGroupByGroupId(groupMemberId.getGroupId());
        groupMember.setGroupMemberState(groupMemberState);
        if (groupMemberState.equals(GroupMemberState.ACCEPT)) {
            groups.addMember();
        }
        return groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMemberId, groupMemberState);
    }

    public Boolean acceptGroupMember(GroupMemberId groupMemberId){
        return answerGroupMember(groupMemberId, GroupMemberState.ACCEPT);
    }

    public Boolean rejectGroupMember(GroupMemberId groupMemberId){
        return answerGroupMember(groupMemberId, GroupMemberState.REJECT);
    }

    public Boolean cancelGroupMember(GroupMemberId groupMemberId){
        return answerGroupMember(groupMemberId, GroupMemberState.CANCEL);
    }


    /**
     * 그룹 탈퇴 요청
     * @param groupMemberId
     * @return result(t/f)
     */
    @Transactional
    public Boolean leaveGroupMember(GroupMemberId groupMemberId) {
        validationBeforeLeaveGroup(groupMemberId);
        GroupMember groupMember = groupMemberRepository.findByGroupMemberIdAndGroupMemberState(groupMemberId, GroupMemberState.ACCEPT).get();
        groupMember.setGroupMemberState(GroupMemberState.LEAVE);
        Groups group = findGroupByGroupId(groupMemberId.getGroupId());
        group.leaveMember();
        return groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMemberId, GroupMemberState.LEAVE);
    }

    /**
     * 그룹장 변경 요청
     * @param groupId 변경하고자 하는 그룹 ID
     * @param memberId 그룹장이 될 그룹원 ID
     * @return result(t/f)
     */
    @Transactional
    public Boolean changeGroupLeaderByGroupIdAndMemberID(Integer groupId, Long memberId) {
        validationGroup(groupId);
        validationMember(memberId);
        GroupMember groupLeader = getGroupLeader(groupId);
        GroupMember targetMember = getGroupMemberByGroupMemberId(new GroupMemberId(groupId, memberId));
        groupLeader.setGroupMemberState(GroupMemberState.ACCEPT);
        targetMember.setGroupMemberState(GroupMemberState.LEADER);
        return getGroupLeader(groupId).equals(targetMember)
                && groupLeader.getGroupMemberState().equals(GroupMemberState.ACCEPT);
    }

    /**
     * 그룹 삭제
     */
    @Transactional
    public Boolean deleteGroup(Integer groupId) {
        validationGroup(groupId);
        List<GroupMember> groupMemberList = groupMemberRepository.findAllMemberByGroupId(groupId);
        List<GroupMember> requestGroupMemberList = groupMemberRepository.findRequestGroupMemberByGroupId(groupId);
        Groups group = findGroupByGroupId(groupId);

        groupMemberList.forEach(groupMember -> groupMember.setGroupMemberState(GroupMemberState.LEAVE));
        requestGroupMemberList.forEach(groupMember -> groupMember.setGroupMemberState(GroupMemberState.REJECT));
        group.delete();
        return group.getIsDeleted().equals(true) && groupMemberRepository.findAllMemberByGroupId(groupId).size() == 0;
    }

    @Transactional
    public Boolean modifyGroupInfo(Groups group, ModifyGroupInfoRequest request) {
        group.changeDescriptionAndPicture(request.getGroupDescription(), request.getGroupPicture());
        return group.getGroupDescription().equals(request.getGroupDescription()) &&
                group.getGroupPicture().equals(request.getGroupPicture());
    }


    /**
     * 그룹장 조회
     * @param groupId 조회하고자 하는 그룹 ID
     * @return GroupMember
     */
    public GroupMember getGroupLeader(Integer groupId) {
        validationGroup(groupId);
        Optional<GroupMember> leader = groupMemberRepository.findGroupLeaderByGroupId(groupId);
        if (leader.isEmpty()) throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        return leader.get();
    }

    /**
     * 그룹원 조회
     * @param groupMemberId 조회하고자 하는 GroupMemberId
     * @return GroupMember
     */
    public GroupMember getGroupMemberByGroupMemberId(GroupMemberId groupMemberId) {
        validationGroup(groupMemberId.getGroupId());
        Optional<GroupMember> groupMember = groupMemberRepository.findByGroupMemberIdAndGroupMemberState(
                groupMemberId, GroupMemberState.ACCEPT);
        if (groupMember.isEmpty()) throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        return groupMember.get();
    }

    public List<GroupMember> findJoinedGroupByMemberId(Long userId) {
        List<GroupMember> groupMemberList = groupMemberRepository.findJoinedGroupByMemberId(userId);
        return groupMemberList.stream()
                .filter(groupMember -> findGroupByGroupId(groupMember.getGroupMemberId().getGroupId()).getIsDeleted().equals(false))
                .collect(Collectors.toList());
    }

    public List<Groups> findNotLeaderGroupByMemberId(Long userId) {
        List<GroupMember> groupMemberList = groupMemberRepository.findJoinedGroupByMemberId(userId);
        return groupMemberList.stream()
                .filter(groupMember -> groupMember.getGroupMemberState() == GroupMemberState.ACCEPT)
                .map(groupMember -> findGroupByGroupId(groupMember.getGroupMemberId().getGroupId()))
                .collect(Collectors.toList());
    }

    /**
     * validation available group
     * @param groupId -> group id
     */
    private void validationGroup(Integer groupId){
        Optional<Groups> group = groupRepository.findById(groupId);
        if (group.isEmpty()) throw new ApiException(ExceptionEnum.GROUPS_NOT_FOUND);
        if (group.get().getIsDeleted()) throw new ApiException(ExceptionEnum.GROUPS_DELETED);
    }

    /**
     * validation available group, exist user
     */
    private void validationMember(Long memberId){
        if (!memberService.existById(memberId)) throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
    }


    /**
     * validation available group, exist user
     */
    private void validationGroupAndUser(GroupMemberId groupMemberId){
        validationGroup(groupMemberId.getGroupId());
        validationMember(groupMemberId.getMemberId());
    }

    /**
     * validation available group, exists user, join request
     * @param groupMemberId -> groupMemberId
     */
    private void validationBeforeAnswerGroupMember(GroupMemberId groupMemberId) {
        validationGroupAndUser(groupMemberId);
        if (!groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMemberId, GroupMemberState.REQUEST))
            throw new ApiException(ExceptionEnum.GROUPS_REQUEST_NOT_FOUND);
    }

    /**
     * validation available group, exists user, groupmember
     * @param groupMemberId -> groupMemberId
     */
    private void validationBeforeLeaveGroup(GroupMemberId groupMemberId) {
        validationGroupAndUser(groupMemberId);
        if (groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMemberId, GroupMemberState.LEADER))
            throw new ApiException(ExceptionEnum.GROUPS_LEADER_CANT_LEAVE);
        if (!groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMemberId, GroupMemberState.ACCEPT))
            throw new ApiException(ExceptionEnum.GROUPS_MEMBER_NOT_FOUND);
    }

    /**
     * validation new group name, exists user,
     * @param group -> group
     */
    private void validationBeforeCreateGroup(Groups group) {
        if (groupRepository.existsByGroupName(group.getGroupName())) throw new ApiException(ExceptionEnum.GROUPS_NAME_ALREADY_EXISTS);
        if (!memberService.existById(group.getGroupLeaderId())) throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
    }

    /**
     * validation available group, exists user, not groupmember;
     * @param groupMemberId -> groupMemberId
     */
    private void validationBeforeJoinGroup(GroupMemberId groupMemberId) {
        validationGroupAndUser(groupMemberId);
        if (groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMemberId, GroupMemberState.LEADER) ||
                groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMemberId, GroupMemberState.ACCEPT))
            throw new ApiException(ExceptionEnum.GROUPS_ALREADY_JOINED);
        if (groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMemberId, GroupMemberState.REQUEST)) {
            throw new ApiException(ExceptionEnum.GROUPS_ALREADY_REQUESTED);
        }
    }

    public Groups findGroupByGroupId(Integer groupId) {
        Optional<Groups> group = groupRepository.findById(groupId);
        if (group.isEmpty()) throw new ApiException(ExceptionEnum.GROUPS_NOT_FOUND);
        if (group.get().getIsDeleted().equals(true)) throw new ApiException(ExceptionEnum.GROUPS_DELETED);
        return group.get();
    }
}
