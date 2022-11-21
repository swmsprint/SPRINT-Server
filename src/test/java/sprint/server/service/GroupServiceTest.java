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
import sprint.server.domain.member.Member;
import sprint.server.repository.GroupMemberRepository;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GroupServiceTest {
    @Autowired GroupService groupService;
    @Autowired MemberService memberService;
    @Autowired GroupMemberRepository groupMemberRepository;

    @Autowired EntityManager em;

    /**
     * 그룹 만들기 테스트
     */
    @Test
    void makeGroupTest(){
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
    void requestGroupJoinTest(){
        /* 정상적인 요청 */
        Groups group = new Groups("groups1", 1L, "Description", "picture");
        groupService.join(group);
        Member member = memberService.findById(2L);
        Boolean result = groupService.requestJoinGroupMember(group, member);

        GroupMember groupMember = new GroupMember(group, member);
        assertEquals(true, result);
        assertEquals(true, groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember.getGroupMemberId(), GroupMemberState.REQUEST));

        /* 이미 해당 그룹에 가입해 있을 때 (그룹장일때) */
        Member member2 = memberService.findById(1L);
        ApiException thrown = assertThrows(ApiException.class, () -> groupService.requestJoinGroupMember(group, member2));
        assertEquals("G0004", thrown.getErrorCode());

        /* 이미 해당 그룹에 가입해 있을 때 (일반 그룹원) */
        Boolean result2 = groupService.answerGroupMember(group, member, GroupMemberState.ACCEPT);

        assertEquals(true, result2);
        ApiException thrown2 = assertThrows(ApiException.class, () -> groupService.requestJoinGroupMember(group, member));
        assertEquals("G0004", thrown2.getErrorCode());
    }

    /**
     * 그룹 가입 요청 승인 테스트
     */
    @Test
    void acceptGroupMemberTest() {
        Groups group = new Groups("groups1", 1L, "Description", "picture");
        groupService.join(group);
        Member member2 = memberService.findById(2L);
        Member member3 = memberService.findById(3L);
        Member member4 = memberService.findById(4L);
        groupService.requestJoinGroupMember(group, member2);
        groupService.requestJoinGroupMember(group, member3);
        groupService.requestJoinGroupMember(group, member4);

        /* ACCEPT TEST */
        /* 정상적인 요청 */
        Boolean result = groupService.answerGroupMember(group, member2, GroupMemberState.ACCEPT);
        assertEquals(true, result);

        GroupMember groupMember = new GroupMember(group, member2);
        assertEquals(true, groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember.getGroupMemberId(), GroupMemberState.ACCEPT));

        /* REJECT TEST */
        /*정상적인 요청 */
        Boolean result2 = groupService.answerGroupMember(group, member3, GroupMemberState.REJECT);
        assertEquals(true, result2);

        GroupMember groupMember2 = new GroupMember(group, member3);
        assertEquals(true, groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember2.getGroupMemberId(), GroupMemberState.REJECT));

        /* CANCEL TEST */
        Boolean result3 = groupService.answerGroupMember(group, member4, GroupMemberState.CANCEL);
        assertEquals(true, result3);

        GroupMember groupMember3 = new GroupMember(group, member4);
        assertEquals(true, groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember3.getGroupMemberId(), GroupMemberState.CANCEL));

        /* 수락/거절할 요청이 존재하지 않을 때 */
        ApiException thrown3 = assertThrows(ApiException.class, () -> groupService.answerGroupMember(group, member3, GroupMemberState.ACCEPT));
        ApiException thrown4 = assertThrows(ApiException.class, () -> groupService.answerGroupMember(group, member3, GroupMemberState.REJECT));
        ApiException thrown5 = assertThrows(ApiException.class, () -> groupService.answerGroupMember(group, member3, GroupMemberState.CANCEL));
        assertEquals("G0003", thrown3.getErrorCode());
        assertEquals("G0003", thrown4.getErrorCode());
        assertEquals("G0003", thrown5.getErrorCode());
    }

    /**
     * 그룹 탈퇴 테스트
     */
    @Test
    void leaveGroupMemberTest(){
        Groups group = new Groups("groups1", 1L, "Description", "picture");
        groupService.join(group);
        Member member1 = memberService.findById(1L);
        Member member2 = memberService.findById(2L);
        groupService.requestJoinGroupMember(group, member2);


        /* 해당 그룹 멤버가 아닐 때 */
        ApiException thrown1 = assertThrows(ApiException.class, () -> groupService.leaveGroupMember(group, member2));
        assertEquals("G0006", thrown1.getErrorCode());

        groupService.answerGroupMember(group, member2, GroupMemberState.ACCEPT);

        /* 정상적인 요청 */
        GroupMember groupMember = groupService.findJoinedGroupMemberByGroupAndMember(group, member2);
        Boolean result = groupService.leaveGroupMember(group, member2);
        assertEquals(true, result);
        assertEquals(true, groupMemberRepository.existsByGroupMemberIdAndGroupMemberState(groupMember.getGroupMemberId(), GroupMemberState.LEAVE));

        /* 해당 그룹 리더가 요청할 때*/
        ApiException thrown3 = assertThrows(ApiException.class, () -> groupService.leaveGroupMember(group, member1));
        assertEquals("G0005", thrown3.getErrorCode());
    }

    /**
     * 그룹장 변경 테스트
     */
    @Test
    void changeGroupLeaderTest() {
        Groups group = new Groups("groups1", 1L, "Description", "picture");
        groupService.join(group);
        Member member1 = memberService.findById(1L);
        Member member2 = memberService.findById(2L);
        GroupMemberId groupMemberId = new GroupMemberId(group.getId(), 2L);
        groupService.requestJoinGroupMember(group, member2);
        groupService.answerGroupMember(group, member2, GroupMemberState.ACCEPT);

        /* 정상적인 요청 */
        Boolean result = groupService.changeGroupLeaderByGroupAndMember(group, member2);
        Member newLeader = groupService.findGroupLeader(group);

        GroupMember groupMember1 = groupService.findJoinedGroupMemberByGroupAndMember(group, member1);
        GroupMember groupMember2 = groupService.findJoinedGroupMemberByGroupAndMember(group, member2);

        assertEquals(true, result);
        assertEquals(2L, newLeader.getId());
        assertEquals(GroupMemberState.ACCEPT, groupMember1.getGroupMemberState());
        assertEquals(GroupMemberState.LEADER, groupMember2.getGroupMemberState());
    }

    /**
     * 그룹장 조회 테스트
     */
    @Test
    void findGroupLeaderByGroupIdTest(){
        Groups group = new Groups("groups1", 1L, "Description", "picture");
        Member member1 = memberService.findById(1L);
        groupService.join(group);

        /* 정상적인 요청 */
        Member leader = groupService.findGroupLeader(group);
        GroupMember groupMember = groupService.findJoinedGroupMemberByGroupAndMember(group, member1);

        assertEquals(1L, leader.getId());
        assertEquals(group.getId(), groupMember.getGroupId());
        assertEquals(GroupMemberState.LEADER, groupMember.getGroupMemberState());

        /* 그룹장 변경 후 테스트 */
        // 새로운 멤버 가입
        Member member2 = memberService.findById(2L);
        groupService.requestJoinGroupMember(group, member2);
        groupService.answerGroupMember(group, member2, GroupMemberState.ACCEPT);

        // 그룹장 변경
        Boolean result = groupService.changeGroupLeaderByGroupAndMember(group, member2);
        Member newLeader = groupService.findGroupLeader(group);
        GroupMember groupMember2 = groupService.findJoinedGroupMemberByGroupAndMember(group, member2);

        assertEquals(true, result);
        assertEquals(2L, newLeader.getId());
        assertEquals(group.getId(), groupMember2.getGroupId());
        assertEquals(GroupMemberState.LEADER, groupMember2.getGroupMemberState());
    }

    /**
     * 그룹 삭제 테스트
     */
    @Test
    void deleteGroupTest() {
        /* 그룹 만든 후 그룹원 가입 */
        Groups group = new Groups("groups1", 1L, "Description", "picture");
        groupService.join(group);
        for (Long i = 2L; i < 5L; i++) {
            Member member = memberService.findById(i);
            groupService.requestJoinGroupMember(group, member);
            groupService.answerGroupMember(group, member, GroupMemberState.ACCEPT);
        }

        /* 정상적인 요청 */
        Boolean result = groupService.deleteGroup(group);
        ApiException thrown2 = assertThrows(ApiException.class , () -> groupService.findGroupByGroupId(group.getId()));
        List<GroupMember> groupMemberList = groupMemberRepository.findAllMemberByGroupId(1);
        assertEquals(true, result);
        assertEquals("G0009", thrown2.getErrorCode());
        assertEquals(0, groupMemberList.size());
    }
}