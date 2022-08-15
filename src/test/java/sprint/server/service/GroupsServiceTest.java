package sprint.server.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.exception.ApiException;
import sprint.server.domain.Groups;
import sprint.server.domain.groupmember.GroupMember;
import sprint.server.domain.groupmember.GroupMemberId;
import sprint.server.domain.groupmember.GroupMemberState;
import sprint.server.repository.GroupMemberRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class GroupsServiceTest {
    @Autowired GroupsService groupsService;
    @Autowired MemberService memberService;
    @Autowired GroupMemberRepository groupMemberRepository;
    @Test
    public void MakeGroupTest(){
        /* 정상적인 요청 */
        Groups groups = new Groups("groups1", memberService.findById(1L), "Description", "picture");
        groupsService.join(groups);

        /* 해당 이름의 그룹이 이미 있을때 */
        Groups groups2 = new Groups("groups1", memberService.findById(1L), "Description", "picture");
        ApiException thrown = assertThrows(ApiException.class, () -> groupsService.join(groups2));
        assertEquals("G0001", thrown.getErrorCode());

        /* 해당 멤버가 없을 때 */
        ApiException thrown2 = assertThrows(ApiException.class, () -> new Groups("groups2", memberService.findById(-1L), "Description", "picture"));
        assertEquals("M0001", thrown2.getErrorCode());
    }

    @Test
    public void RequestGroupJoinTest(){
        /* 정상적인 요청 */
        Groups groups = new Groups("groups1", memberService.findById(1L), "Description", "picture");
        groupsService.join(groups);
        GroupMember groupMember = new GroupMember(new GroupMemberId(groups, memberService.findById(1L)));
        Boolean result = groupsService.joinGroupMember(groupMember);
        assertEquals(true, result);
        assertEquals(true, groupMemberRepository.existsByGroupMemberIdAndMemberState(new GroupMemberId(groups, memberService.findById(1L)), GroupMemberState.REQUEST));
    }
}