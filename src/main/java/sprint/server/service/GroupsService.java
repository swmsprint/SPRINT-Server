package sprint.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.Groups;
import sprint.server.domain.groupmember.GroupMember;
import sprint.server.domain.groupmember.GroupMemberState;
import sprint.server.repository.GroupMemberRepository;
import sprint.server.repository.GroupRepository;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupsService {

    private final MemberService memberService;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    @Transactional
    public int join(Groups groups) {
        if(existsByGroupName(groups.getGroupName())) {
            throw new ApiException(ExceptionEnum.GROUPS_ALREADY_EXISTS);
        } else if (groups.getMember().equals(null)){
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        } else {
            groupRepository.save(groups);
            return groups.getId();
        }
    }

    @Transactional
    public Boolean joinGroupMember(GroupMember groupMember) {
        /* Validation 필요 */
        groupMemberRepository.save(groupMember);
        return groupMemberRepository.existsByGroupMemberIdAndMemberState(groupMember.getGroupMemberId(), GroupMemberState.REQUEST);
    }

    private Boolean existsById(Integer groupId) {
        return groupRepository.existsById(groupId);
    }

    public Boolean existsByGroupName(String GroupName){
        return groupRepository.existsByGroupName(GroupName);
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
