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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class GroupServiceTest {
    @Autowired GroupService groupService;
    @Autowired MemberService memberService;
    @Autowired GroupMemberRepository groupMemberRepository;

    /**
     * 그룹 만들기 테스트
     */
    @Test
    public void makeGroupTest(){
        /* 정상적인 요청 */
        Groups groups = new Groups("groups1", 2L, "Description", "picture");
        groupService.join(groups);

        /* 해당 이름의 그룹이 이미 있을때 */
        Groups groups2 = new Groups("groups1", 1L, "Description", "picture");
        ApiException thrown = assertThrows(ApiException.class, () -> groupService.join(groups2));
        assertEquals("G0001", thrown.getErrorCode());

        /* 해당 멤버가 없을 때 */
        Groups groups3 = new Groups("groups2", -1L, "Description", "picture");
        ApiException thrown2 = assertThrows(ApiException.class, () -> groupService.join(groups3));
        assertEquals("M0001", thrown2.getErrorCode());
    }

    /**
     * 그룹 가입 요청 테스트
     */
    @Test
    public void requestGroupJoinTest(){
        /* 정상적인 요청 */
        Groups groups = new Groups("groups1", 1L, "Description", "picture");
        groupService.join(groups);
        GroupMember groupMember = new GroupMember(new GroupMemberId(groups.getId(), 2L));
        Boolean result = groupService.requestJoinGroupMember(groupMember);

        assertEquals(true, result);
        assertEquals(true, groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember.getGroupMemberId(), GroupMemberState.REQUEST));

        /* 이미 해당 그룹에 가입해 있을 때 (그룹장일때) */
        GroupMember groupMember2 = new GroupMember(new GroupMemberId(groups.getId(),1L));
        ApiException thrown = assertThrows(ApiException.class, () -> groupService.requestJoinGroupMember(groupMember2));
        assertEquals("G0004", thrown.getErrorCode());

        /* 이미 해당 그룹에 가입해 있을 때 (일반 그룹원) */
        Boolean result2 = groupService.acceptGroupMember(groupMember.getGroupMemberId());

        assertEquals(true, result2);
        ApiException thrown2 = assertThrows(ApiException.class, () -> groupService.requestJoinGroupMember(groupMember2));
        assertEquals("G0004", thrown2.getErrorCode());
    }

    /**
     * 그룹 가입 요청 승인 테스트
     */
    @Test
    public void acceptGroupMemberTest() {
        Groups groups = new Groups("groups1", 1L, "Description", "picture");
        groupService.join(groups);
        GroupMember groupMember = new GroupMember(new GroupMemberId(groups.getId(), 2L));
        GroupMember groupMember2 = new GroupMember(new GroupMemberId(groups.getId(), 3L));
        GroupMember groupMember3 = new GroupMember(new GroupMemberId(groups.getId(), 4L));
        groupService.requestJoinGroupMember(groupMember);
        groupService.requestJoinGroupMember(groupMember2);
        groupService.requestJoinGroupMember(groupMember3);

        /* ACCEPT TEST */
        /* 정상적인 요청 */
        Boolean result = groupService.acceptGroupMember(groupMember.getGroupMemberId());
        assertEquals(true, result);
        assertEquals(true, groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember.getGroupMemberId(), GroupMemberState.ACCEPT));

        /* REJECT TEST */
        /*정상적인 요청 */
        Boolean result2 = groupService.rejectGroupMember(groupMember2.getGroupMemberId());
        assertEquals(true, result2);
        assertEquals(true, groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember2.getGroupMemberId(), GroupMemberState.REJECT));

        /* CANCEL TEST */
        Boolean result3 = groupService.cancelGroupMember(groupMember3.getGroupMemberId());
        assertEquals(true, result3);
        assertEquals(true, groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember3.getGroupMemberId(), GroupMemberState.CANCEL));

        /* 해당 그룹이 없을 때 */
        ApiException thrown = assertThrows(ApiException.class,() -> groupService.acceptGroupMember(new GroupMemberId(-1, 2L)));
        assertEquals("G0002", thrown.getErrorCode());

        /* 해당 멤버가 없을 때*/
        ApiException thrown2 = assertThrows(ApiException.class, () -> groupService.acceptGroupMember(new GroupMemberId(groups.getId(), -1L)));
        assertEquals("M0001", thrown2.getErrorCode());

        /* 수락/거절할 요청이 존재하지 않을 때 */
        ApiException thrown3 = assertThrows(ApiException.class, () -> groupService.acceptGroupMember(new GroupMemberId(groups.getId(),  3L)));
        assertEquals("G0003", thrown3.getErrorCode());
    }

    /**
     * 그룹 탈퇴 테스트
     */
    @Test
    public void leaveGroupMemberTest(){
        Groups groups = new Groups("groups1", 1L, "Description", "picture");
        groupService.join(groups);
        GroupMemberId groupMemberId = new GroupMemberId(groups.getId(), 2L);
        GroupMember groupMember = new GroupMember(groupMemberId);
        groupService.requestJoinGroupMember(groupMember);


        /* 해당 그룹 멤버가 아닐 때 */
        ApiException thrown1 = assertThrows(ApiException.class, () -> groupService.leaveGroupMember(groupMemberId));
        assertEquals("G0006", thrown1.getErrorCode());

        groupService.acceptGroupMember(groupMemberId);
        /* 정상적인 요청 */
        Boolean result = groupService.leaveGroupMember(groupMemberId);
        assertEquals(true, result);
        assertEquals(true, groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMemberId, GroupMemberState.LEAVE));

        /* 해당 그룹 리더가 요청할 때*/
        ApiException thrown3 = assertThrows(ApiException.class, () -> groupService.leaveGroupMember(new GroupMemberId(groups.getId(),  1L)));
        assertEquals("G0005", thrown3.getErrorCode());
    }

    /**
     * 그룹장 변경 테스트
     */
    @Test
    public void changeGroupLeaderTest() {
        Groups groups = new Groups("groups1", 1L, "Description", "picture");
        groupService.join(groups);
        GroupMemberId groupMemberId = new GroupMemberId(groups.getId(), 2L);
        GroupMember groupMember = new GroupMember(groupMemberId);
        groupService.requestJoinGroupMember(groupMember);
        groupService.acceptGroupMember(groupMemberId);

        /* 정상적인 요청 */
        Boolean result = groupService.changeGroupLeaderByGroupIdAndMemberID(groups.getId(), 2L);
        GroupMember newLeader = groupService.getGroupLeader(groups.getId());
        assertEquals(true, result);
        assertEquals(memberService.findById(2L).getId(), newLeader.getGroupMemberId().getMemberId());
        assertEquals(groups.getId(), newLeader.getGroupMemberId().getGroupId());
        assertEquals(GroupMemberState.LEADER, newLeader.getGroupMemberState());
    }

    /**
     * 그룹장 조회 테스트
     */
    @Test
    public void findGroupLeaderByGroupIdTest(){
        Groups groups = new Groups("groups1", 1L, "Description", "picture");
        groupService.join(groups);
        GroupMember groupMember = groupService.getGroupLeader(groups.getId());
        assertEquals(1L, groupMember.getGroupMemberId().getMemberId());
        assertEquals(groups.getId(), groupMember.getGroupMemberId().getGroupId());
        assertEquals(GroupMemberState.LEADER, groupMember.getGroupMemberState());

        /* 그룹장 변경 후 테스트 */
        // 새로운 멤버 가입
        GroupMemberId groupMemberId = new GroupMemberId(groups.getId(), 2L);
        GroupMember groupMember2 = new GroupMember(groupMemberId);
        groupService.requestJoinGroupMember(groupMember2);
        groupService.acceptGroupMember(groupMemberId);
        // 그룹장 변경
        Boolean result = groupService.changeGroupLeaderByGroupIdAndMemberID(groups.getId(), 2L);
        GroupMember newLeader = groupService.getGroupLeader(groups.getId());
        assertEquals(true, result);
        assertEquals(2L, newLeader.getGroupMemberId().getMemberId());
        assertEquals(groups.getId(), newLeader.getGroupMemberId().getGroupId());
        assertEquals(GroupMemberState.LEADER, newLeader.getGroupMemberState());
    }

    /**
     * 그룹 삭제 테스트
     */
    @Test
    public void deleteGroupTest() {
        /* 그룹 만든 후 그룹원 가입 */
        Groups groups = new Groups("groups1", 1L, "Description", "picture");
        groupService.join(groups);
        for (Long i = 2L; i < 5L; i++) {
            GroupMemberId groupMemberId = new GroupMemberId(groups.getId(), i);
            GroupMember groupMember = new GroupMember(groupMemberId);
            groupService.requestJoinGroupMember(groupMember);
            groupService.acceptGroupMember(groupMemberId);
        }

        /* 해당 그룹이 존재하지 않을 때 */
        ApiException thrown = assertThrows(ApiException.class, () -> groupService.deleteGroup(2));
        assertEquals("G0002", thrown.getErrorCode());

        /* 정상적인 요청 */
        Boolean result = groupService.deleteGroup(groups.getId());
        ApiException thrown2 = assertThrows(ApiException.class , () -> groupService.getGroupLeader(groups.getId()));
        List<GroupMember> groupMemberList = groupMemberRepository.findAllMemberByGroupId(1);
        assertEquals(true, result);
        assertEquals("G0009", thrown2.getErrorCode());
        assertEquals(0, groupMemberList.size());
    }
}