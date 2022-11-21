package sprint.server.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.exception.ApiException;
import sprint.server.domain.friend.FriendState;
import sprint.server.domain.friend.Friend;
import sprint.server.domain.member.Member;
import sprint.server.repository.FriendRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FriendServiceTest {

    @Autowired
    FriendService friendService;
    @Autowired
    FriendRepository friendRepository;
    @Autowired MemberService memberService;

    @Test
    void Test() {
        Member member1 = memberService.findById(1L);
        Member member2 = memberService.findById(2L);
        friendService.requestFriends(member1, member2);
        Optional<Friend> friends1 = friendService.findByTwoMemberIdAndEstablishState(2L, 1L, FriendState.REQUEST);
        if (friends1.isPresent()){
            assertEquals(1L, friends1.get().getSourceMemberId());
            assertEquals(2L, friends1.get().getTargetMemberId());
        }
        assertEquals(friends1, friendService.findByTwoMemberIdAndEstablishState(2L, 1L, FriendState.REQUEST));
    }

    /**
     * 친구 요청 테스트
     */
    @Test
    void friendsRequestTest(){
        /* 정상적인 요청 */
        Member member1 = memberService.findById(1L);
        Member member2 = memberService.findById(2L);
        Member member3 = memberService.findById(3L);
        Boolean requestResult = friendService.requestFriends(member1, member2);
        Friend friend1 = friendRepository.findBySourceMemberIdAndTargetMemberId(1L, 2L).get();
        assertEquals(true, requestResult);
        assertEquals(1L, friend1.getSourceMemberId());
        assertEquals(2L, friend1.getTargetMemberId());
        assertEquals(FriendState.REQUEST, friend1.getEstablishState());

        /* 이미 해당 친구 추가 요청이 존재할 경우*/
        ApiException thrown = assertThrows(ApiException.class, () -> friendService.requestFriends(member1, member2));
        assertEquals("F0002", thrown.getErrorCode());

        /* 이미 둘이 친구인 경우*/
        friendService.acceptFriendsRequest(member1, member2);
        ApiException thrown2 = assertThrows(ApiException.class, () -> friendService.requestFriends(member1, member2));
        assertEquals("F0003", thrown2.getErrorCode());

        /* 이미 상대방으로부터 친구 추가 요청이 존재할 경우 */
        Boolean requestResult2 = friendService.requestFriends(member1, member3);
        Boolean requestResult3 = friendService.requestFriends(member3, member1);
        Friend friend2 = friendRepository.findBySourceMemberIdAndTargetMemberId(1L, 3L).get();
        assertEquals(1L, friend2.getSourceMemberId());
        assertEquals(3L, friend2.getTargetMemberId());
        assertEquals(FriendState.ACCEPT, friend2.getEstablishState());
        assertEquals(true, requestResult2);
        assertEquals(true, requestResult3);
        assertEquals(true, friendRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 3L, FriendState.ACCEPT));
    }

    /**
     * 친구 추가 거절 요청 테스트
     */
    @Test
    void rejectFriendsRequestTest() {
        Member member1 = memberService.findById(1L);
        Member member2 = memberService.findById(2L);
        /* 해당 친구 추가 요청이 존재하지 없을 때 */
        ApiException thrown = assertThrows(ApiException.class, () -> friendService.rejectFriendsRequest(member1, member2));
        assertEquals("F0001", thrown.getErrorCode());

        /* 정상적인 요청 */
        Boolean result1 = friendService.requestFriends(member1, member2);
        Boolean result2 = friendService.rejectFriendsRequest(member1, member2);
        Friend friend = friendRepository.findBySourceMemberIdAndTargetMemberId(1L, 2L).get();
        assertEquals(1L, friend.getSourceMemberId());
        assertEquals(2L, friend.getTargetMemberId());
        assertEquals(FriendState.REJECT , friend.getEstablishState());
        assertEquals(true, result1);
        assertEquals(true, result2);
    }

    /**
     * 친구 추가 수락 요청 테스트
     */
    @Test
    void acceptFriendsRequestTest() {
        Member member1 = memberService.findById(1L);
        Member member2 = memberService.findById(2L);
        /* 해당 친구 추가 요청이 존재하지 없을 때 */
        ApiException thrown = assertThrows(ApiException.class, () -> friendService.acceptFriendsRequest(member1, member2));
        assertEquals("F0001", thrown.getErrorCode());

        /* 정상적인 요청 */
        Boolean result1 = friendService.requestFriends(member1, member2);
        Boolean result2 = friendService.acceptFriendsRequest(member1, member2);
        Friend friend = friendRepository.findBySourceMemberIdAndTargetMemberId(1L, 2L).get();
        assertEquals(FriendState.ACCEPT, friend.getEstablishState());
        assertEquals(true, friendRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 2L, FriendState.ACCEPT));
        assertEquals(true, result1);
        assertEquals(true, result2);

        /* 이미 둘이 친구인 경우 */
        ApiException thrown2 = assertThrows(ApiException.class, () -> friendService.acceptFriendsRequest(member1, member2));
        assertEquals("F0003", thrown2.getErrorCode());
    }

    /**
     * 친구 제거 요청 테스트
     */
    @Test
    void deleteFriendsTest() {
        /* 둘이 친구 관계가 아닌 경우*/
        Member sourceMember = memberService.findById(1L);
        Member targetMember = memberService.findById(2L);
        ApiException thrown = assertThrows(ApiException.class, () -> friendService.deleteFriends(sourceMember, targetMember));
        assertEquals("F0004", thrown.getErrorCode());

        /* 정상적인 요청 */
        friendService.requestFriends(sourceMember, targetMember);
        friendService.acceptFriendsRequest(sourceMember, targetMember);
        friendService.deleteFriends(sourceMember, targetMember);
        assertEquals(true, friendRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 2L, FriendState.DELETE));
        assertEquals(false, friendRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 2L, FriendState.ACCEPT));
    }

    @Test
    void cancelFriendsTest() {
        Member member1 = memberService.findById(1L);
        Member member2 = memberService.findById(2L);
        /* 해당 친구 추가 요청이 존재하지 없을 때 */
        ApiException thrown = assertThrows(ApiException.class, () -> friendService.cancelFriends(member1, member2));
        assertEquals("F0001", thrown.getErrorCode());

        /* 정상적인 요청 */
        friendService.requestFriends(member1, member2);
        Boolean result = friendService.cancelFriends(member1, member2);
        assertEquals(true, friendRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 2L, FriendState.CANCEL));
        assertEquals(true, result);
    }

    /**
     * 친구 목록 요청 테스트
     */
    @Test
    void friendsListTest() {
        Member member1 = memberService.findById(1L);
        Member member2 = memberService.findById(2L);
        Member member3 = memberService.findById(3L);
        Member member4 = memberService.findById(4L);

        /* 정상적인 요청 */
        friendService.requestFriends(member1, member2);
        friendService.requestFriends(member3, member1);
        friendService.requestFriends(member4, member1);
        friendService.requestFriends(member4, member2);
        friendService.acceptFriendsRequest(member1, member2);
        friendService.acceptFriendsRequest(member3, member1);
        friendService.acceptFriendsRequest(member4, member1);
        friendService.acceptFriendsRequest(member4, member2);
        assertEquals(3, friendService.findFriendsByMemberId(member1, FriendState.ACCEPT).size());
        assertEquals(2, friendService.findFriendsByMemberId(member2, FriendState.ACCEPT).size());
        assertEquals(1, friendService.findFriendsByMemberId(member3, FriendState.ACCEPT).size());
        assertEquals(2, friendService.findFriendsByMemberId(member4, FriendState.ACCEPT).size());

        /* 친구가 계정을 비활성화할 때 */
        memberService.disableMember(member2); // 비활성화
        assertEquals(2, friendService.findFriendsByMemberId(member1, FriendState.ACCEPT).size());
        assertEquals(1, friendService.findFriendsByMemberId(member3, FriendState.ACCEPT).size());
        assertEquals(1, friendService.findFriendsByMemberId(member4, FriendState.ACCEPT).size());
    }

    /**
     * 친구 목록 요청 테스트 (내가 보낸 친구 요청)
     */
    @Test
    void friendsListSentTest() {
        Member member1 = memberService.findById(1L);
        Member member2 = memberService.findById(2L);
        Member member3 = memberService.findById(3L);
        Member member4 = memberService.findById(4L);
        Member member5 = memberService.findById(5L);

        /* 정상적인 요청 */
        friendService.requestFriends(member1, member2);
        friendService.requestFriends(member1, member3);
        friendService.requestFriends(member1, member4);
        friendService.requestFriends(member1, member5);
        assertEquals(4, friendService.findFriendsByMemberId(member1, FriendState.REQUEST).size());
    }

    /**
     * 친구 목록 요청 테스트 (내가 받은 친구 요청)
     */
    @Test
    void friendsListReceivedTest() {
        Member member1 = memberService.findById(1L);
        Member member2 = memberService.findById(2L);
        Member member3 = memberService.findById(3L);
        Member member4 = memberService.findById(4L);

        /* 정상적인 요청 */
        friendService.requestFriends(member3, member1);
        friendService.requestFriends(member1, member2);
        friendService.requestFriends(member4, member2);
        assertEquals(1, friendService.findByTargetMemberIdAndFriendState(member1, FriendState.REQUEST).size());
        assertEquals(2, friendService.findByTargetMemberIdAndFriendState(member2, FriendState.REQUEST).size());
    }
}