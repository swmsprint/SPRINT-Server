package sprint.server.service;

import io.swagger.annotations.Api;
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

    /**
     * 그룹 만들기 테스트
     */
    @Test
    public void makeGroupTest(){
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

    /**
     * 그룹 가입 요청 테스트
     */
    @Test
    public void requestGroupJoinTest(){
        /* 정상적인 요청 */
        Groups groups = new Groups("groups1", memberService.findById(1L), "Description", "picture");
        groupsService.join(groups);
        GroupMember groupMember = new GroupMember(new GroupMemberId(groups, memberService.findById(2L)));
        Boolean result = groupsService.joinGroupMember(groupMember);
        assertEquals(true, result);
        assertEquals(true, groupMemberRepository.existsByGroupMemberIdAndMemberState(new GroupMemberId(groups, memberService.findById(2L)), GroupMemberState.REQUEST));

        /* 이미 해당 그룹에 가입해 있을 때 */
        GroupMember groupMember2 = new GroupMember(new GroupMemberId(groups, memberService.findById(1L)));
        ApiException thrown = assertThrows(ApiException.class, () -> groupsService.joinGroupMember(groupMember2));
        assertEquals("G0004", thrown.getErrorCode());
    }

    /**
     * 그룹 가입 요청 응답 테스트
     */
    @Test
    public void answerGroupMemberTest() {
        /* 정상적인 요청 */
        Groups groups = new Groups("groups1", memberService.findById(1L), "Description", "picture");
        groupsService.join(groups);
        GroupMember groupMember = new GroupMember(new GroupMemberId(groups, memberService.findById(2L)));
        groupsService.joinGroupMember(groupMember);
        Boolean result = groupsService.answerGroupMember(groups.getId(), 2L, true);
        assertEquals(true, result);

        /* 해당 그룹이 없을 때 */
        ApiException thrown = assertThrows(ApiException.class,() -> groupsService.answerGroupMember(-1, 2L, true));
        assertEquals("G0002", thrown.getErrorCode());

        /* 해당 멤버가 없을 때*/
        ApiException thrown2 = assertThrows(ApiException.class, () -> groupsService.answerGroupMember(groups.getId(), -1L, true));
        assertEquals("M0001", thrown2.getErrorCode());

        /* 수락/거절할 요청이 존재하지 않을 때 */
        ApiException thrown3 = assertThrows(ApiException.class, () -> groupsService.answerGroupMember(groups.getId(),  3L, true));
        assertEquals("G0003", thrown3.getErrorCode());
    }
}