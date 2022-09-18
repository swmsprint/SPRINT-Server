package sprint.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.Groups;
import sprint.server.domain.groupmember.GroupMember;
import sprint.server.domain.groupmember.GroupMemberId;
import sprint.server.domain.groupmember.GroupMemberState;
import sprint.server.repository.GroupMemberRepository;
import sprint.server.repository.GroupRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


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
     * @param acceptance
     * @return result(t/f)
     */
    @Transactional
    public Boolean answerGroupMember(GroupMemberId groupMemberId, Boolean acceptance) {
        validationBeforeAnswerGroupMember(groupMemberId);
        GroupMember groupMember = groupMemberRepository.findById(groupMemberId).get();
        GroupMemberState groupMemberState = acceptance ? GroupMemberState.ACCEPT: GroupMemberState.REJECT;
        groupMember.setGroupMemberState(groupMemberState);
        return groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMemberId, groupMemberState);
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
        List<GroupMember> groupMemberList = groupMemberRepository.findGroupMemberByGroupId(groupId);
        Groups group = getGroup(groupId);

        groupMemberList.forEach(groupMember -> groupMember.setGroupMemberState(GroupMemberState.LEAVE));
        group.delete();
        return group.getIsDeleted().equals(true) && groupMemberRepository.findGroupMemberByGroupId(groupId).size() == 0;
    }

    /**
     * 그룹 조회
     */
    public Groups getGroup(Integer groupId) {
        validationGroup(groupId);
        Optional<Groups> group = groupRepository.findById(groupId);
        if (group.isEmpty()) throw new ApiException(ExceptionEnum.GROUPS_NOT_FOUND);
        return group.get();
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
    }
}
