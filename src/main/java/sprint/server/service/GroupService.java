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
     * @return : group id
     */
    @Transactional
    public Integer join(Groups groups) {
        validationBeforeCreateGroup(groups);
        groupRepository.save(groups);
        GroupMember groupMember = new GroupMember(new GroupMemberId(groups.getId(), groups.getGroupLeaderId()));
        groupMember.setMemberState(GroupMemberState.LEADER);
        groupMember.setRegisteredDate(Timestamp.valueOf(LocalDateTime.now()));
        groupMemberRepository.save(groupMember);
        return groups.getId();
    }

    /**
     * 그룹 가입 요청
     * @param groupMember
     * @return
     */
    @Transactional
    public Boolean requestJoinGroupMember(GroupMember groupMember) {
        validationBeforeJoinGroup(groupMember.getGroupMemberId());
        groupMemberRepository.save(groupMember);
        return groupMemberRepository.existsByGroupMemberIdAndMemberState(groupMember.getGroupMemberId(), GroupMemberState.REQUEST);
    }

    /**
     * 그룹 가입 요청 응답
     * @param groupMemberId
     * @param acceptance
     * @return
     */
    @Transactional
    public Boolean answerGroupMember(GroupMemberId groupMemberId, Boolean acceptance) {
        validationBeforeAnswerGroupMember(groupMemberId);
        GroupMember groupMember = groupMemberRepository.findById(groupMemberId).get();
        GroupMemberState groupMemberState = acceptance ? GroupMemberState.ACCEPT: GroupMemberState.REJECT;
        groupMember.setMemberState(groupMemberState);
        groupMember.setRegisteredDate(Timestamp.valueOf(LocalDateTime.now()));
        return groupMemberRepository.existsByGroupMemberIdAndMemberState(groupMemberId, groupMemberState);
    }

    /**
     * 그룹 탈퇴 요청
     * @param groupMemberId
     * @return
     */
    @Transactional
    public Boolean leaveGroupMember(GroupMemberId groupMemberId) {
        validationBeforeLeaveGroup(groupMemberId);
        GroupMember groupMember = groupMemberRepository.findByGroupMemberIdAndMemberState(groupMemberId, GroupMemberState.ACCEPT).get();
        groupMember.setMemberState(GroupMemberState.LEAVE);
        return groupMemberRepository.existsByGroupMemberIdAndMemberState(groupMemberId, GroupMemberState.LEAVE);
    }


    /**
     * validation exists group, user, group join request
     */
    private void existsGroupAndMemberByGroupMemberId(GroupMemberId groupMemberId){
        if (!groupRepository.existsById(groupMemberId.getGroupId())) {throw new ApiException(ExceptionEnum.GROUPS_NOT_FOUND);}
        if (!memberService.existById(groupMemberId.getMemberId())) {throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);}
    }

    private void validationBeforeAnswerGroupMember(GroupMemberId groupMemberId) {
        existsGroupAndMemberByGroupMemberId(groupMemberId);
        if (!groupMemberRepository.existsByGroupMemberIdAndMemberState(groupMemberId, GroupMemberState.REQUEST)) {
            throw new ApiException(ExceptionEnum.GROUPS_REQUEST_NOT_FOUND);}
    }

    private void validationBeforeLeaveGroup(GroupMemberId groupMemberId) {
        existsGroupAndMemberByGroupMemberId(groupMemberId);
        if (groupMemberRepository.existsByGroupMemberIdAndMemberState(groupMemberId, GroupMemberState.LEADER)) {
            throw new ApiException(ExceptionEnum.GROUPS_LEADER_CANT_LEAVE);}
        if (!groupMemberRepository.existsByGroupMemberIdAndMemberState(groupMemberId, GroupMemberState.ACCEPT)) {
            throw new ApiException(ExceptionEnum.GROUPS_MEMBER_NOT_FOUND);
        }
    }

    private void validationBeforeCreateGroup(Groups groups) {
        if (existsByGroupName(groups.getGroupName())) {throw new ApiException(ExceptionEnum.GROUPS_NAME_ALREADY_EXISTS);}
        if (!memberService.existById(groups.getGroupLeaderId())) {throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);}
    }

    private void validationBeforeJoinGroup(GroupMemberId groupMemberId) {
        existsGroupAndMemberByGroupMemberId(groupMemberId);
        if (groupMemberRepository.existsByGroupMemberIdAndMemberState(groupMemberId, GroupMemberState.LEADER) ||
                groupMemberRepository.existsByGroupMemberIdAndMemberState(groupMemberId, GroupMemberState.ACCEPT)) {
            throw new ApiException(ExceptionEnum.GROUPS_ALREADY_JOINED);}
    }

    public Boolean existsByGroupName(String groupName){
        return groupRepository.existsByGroupName(groupName);
    }
}
