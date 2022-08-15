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
import sprint.server.domain.member.Member;
import sprint.server.repository.GroupMemberRepository;
import sprint.server.repository.GroupRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupsService {

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
        if(existsByGroupName(groups.getGroupName())) {
            throw new ApiException(ExceptionEnum.GROUPS_ALREADY_EXISTS);
        } else if (groups.getMember() == null){
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        } else {
            groupRepository.save(groups);
            GroupMember groupMember = new GroupMember(new GroupMemberId(groups, groups.getMember()));
            groupMember.setMemberState(GroupMemberState.LEADER);
            groupMember.setRegisteredDate(Timestamp.valueOf(LocalDateTime.now()));
            groupMemberRepository.save(groupMember);
            return groups.getId();
        }
    }

    /**
     * 그룹 가입 요청
     * @param groupMember : GroupMember
     * @return result(true/false)
     */
    @Transactional
    public Boolean joinGroupMember(GroupMember groupMember) {
        if (groupMemberRepository.existsByGroupMemberIdAndMemberState(groupMember.getGroupMemberId(), GroupMemberState.LEADER) ||
                groupMemberRepository.existsByGroupMemberIdAndMemberState(groupMember.getGroupMemberId(), GroupMemberState.ACCEPT)) {
            throw new ApiException(ExceptionEnum.GROUPS_ALREADY_JOINED);
        }
        groupMemberRepository.save(groupMember);
        return groupMemberRepository.existsByGroupMemberIdAndMemberState(groupMember.getGroupMemberId(), GroupMemberState.REQUEST);
    }

    /**
     * 그룹 가입 요청 응답
     * @param groupId : 그룹 id
     * @param userId : Member id
     * @param acceptance : 수락/거절 여부 (true/false)
     * @return result(ture/false)
     */
    @Transactional
    public Boolean answerGroupMember(Integer groupId, Long userId, Boolean acceptance) {
        if (!groupRepository.existsById(groupId)) {
            throw new ApiException(ExceptionEnum.GROUPS_NOT_FOUND);
        } else if (!memberService.existById(userId)) {
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        }
        Groups groups = groupRepository.findById(groupId).get();
        Member member = memberService.findById(userId);
        GroupMemberId groupMemberId = new GroupMemberId(groups, member);
        Optional<GroupMember> groupMember = groupMemberRepository.findByGroupMemberIdAndMemberState(groupMemberId, GroupMemberState.REQUEST);
        if (groupMember.isEmpty()){
            throw new ApiException(ExceptionEnum.GROUPS_REQUEST_NOT_FOUND);
        }
        GroupMemberState groupMemberState = acceptance ? GroupMemberState.ACCEPT: GroupMemberState.REJECT;
        groupMember.get().setMemberState(groupMemberState);
        groupMember.get().setRegisteredDate(Timestamp.valueOf(LocalDateTime.now()));
        return groupMemberRepository.existsByGroupMemberIdAndMemberState(groupMemberId, groupMemberState);
    }

    private Boolean existsById(Integer groupId) {
        return groupRepository.existsById(groupId);
    }

    public Boolean existsByGroupName(String groupName){
        return groupRepository.existsByGroupName(groupName);
    }

    public List<Groups> findByGroupNameContaining(String groupName) {
        return groupRepository.findByGroupNameContaining(groupName);
    }

    public Groups findById(Integer groupId) {
        Optional<Groups> groups = groupRepository.findById(groupId);
        if (groups.isPresent()) {
            return groups.get();
        } else {
            throw new ApiException(ExceptionEnum.GROUPS_NOT_FOUND);
        }
    }
}
