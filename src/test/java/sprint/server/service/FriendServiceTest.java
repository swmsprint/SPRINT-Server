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
        friendService.requestFriends(1L, 2L);
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
        Boolean requestResult = friendService.requestFriends(1L, 2L);
        Friend friend1 = friendRepository.findBySourceMemberIdAndTargetMemberId(1L, 2L).get();
        assertEquals(true, requestResult);
        assertEquals(1L, friend1.getSourceMemberId());
        assertEquals(2L, friend1.getTargetMemberId());
        assertEquals(FriendState.REQUEST, friend1.getEstablishState());

        /* 이미 해당 친구 추가 요청이 존재할 경우*/
        ApiException thrown = assertThrows(ApiException.class, () -> friendService.requestFriends(1L, 2L));
        assertEquals("F0002", thrown.getErrorCode());

        /* 이미 둘이 친구인 경우*/
        friendService.acceptFriendsRequest(1L, 2L);
        ApiException thrown2 = assertThrows(ApiException.class, () -> friendService.requestFriends(1L, 2L));
        assertEquals("F0003", thrown2.getErrorCode());

        /* 이미 상대방으로부터 친구 추가 요청이 존재할 경우 */
        Boolean requestResult2 = friendService.requestFriends(1L, 3L);
        Boolean requestResult3 = friendService.requestFriends(3L, 1L);
        Friend friend2 = friendRepository.findBySourceMemberIdAndTargetMemberId(1L, 3L).get();
        assertEquals(1L, friend2.getSourceMemberId());
        assertEquals(3L, friend2.getTargetMemberId());
        assertEquals(FriendState.ACCEPT, friend2.getEstablishState());
        assertEquals(true, requestResult2);
        assertEquals(true, requestResult3);
        assertEquals(true, friendRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 3L, FriendState.ACCEPT));

        /* 해당 유저가 존재하지 않은 경우 */
        ApiException thrown3 = assertThrows(ApiException.class, () -> friendService.requestFriends(-1L, 1L));
        assertEquals("Source Member가 존재하지 않습니다.", thrown3.getMessage());
        ApiException thrown4 = assertThrows(ApiException.class, () -> friendService.requestFriends(1L, -1L));
        assertEquals("Target Member가 존재하지 않습니다.", thrown4.getMessage());
    }

    /**
     * 친구 추가 거절 요청 테스트
     */
    @Test
    void rejectFriendsRequestTest() {
        /* 해당 친구 추가 요청이 존재하지 없을 때 */
        ApiException thrown = assertThrows(ApiException.class, () -> friendService.rejectFriendsRequest(1L, 2L));
        assertEquals("F0001", thrown.getErrorCode());

        /* 정상적인 요청 */
        Boolean result1 = friendService.requestFriends(1L, 2L);
        Boolean result2 = friendService.rejectFriendsRequest(1L, 2L);
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
        /* 해당 친구 추가 요청이 존재하지 없을 때 */
        ApiException thrown = assertThrows(ApiException.class, () -> friendService.acceptFriendsRequest(1L, 2L));
        assertEquals("F0001", thrown.getErrorCode());

        /* 정상적인 요청 */
        Boolean result1 = friendService.requestFriends(1L, 2L);
        Boolean result2 = friendService.acceptFriendsRequest(1L, 2L);
        Friend friend = friendRepository.findBySourceMemberIdAndTargetMemberId(1L, 2L).get();
        assertEquals(FriendState.ACCEPT, friend.getEstablishState());
        assertEquals(true, friendRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 2L, FriendState.ACCEPT));
        assertEquals(true, result1);
        assertEquals(true, result2);

        /* 이미 둘이 친구인 경우 */
        ApiException thrown2 = assertThrows(ApiException.class, () -> friendService.acceptFriendsRequest(1L, 2L));
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
        friendService.requestFriends(1L, 2L);
        friendService.acceptFriendsRequest(1L, 2L);
        friendService.deleteFriends(sourceMember, targetMember);
        assertEquals(true, friendRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 2L, FriendState.DELETE));
        assertEquals(false, friendRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 2L, FriendState.ACCEPT));
    }

    @Test
    void cancelFriendsTest() {
        /* 해당 친구 추가 요청이 존재하지 없을 때 */
        ApiException thrown = assertThrows(ApiException.class, () -> friendService.cancelFriends(1L, 2L));
        assertEquals("F0001", thrown.getErrorCode());

        /* 정상적인 요청 */
        friendService.requestFriends(1L, 2L);
        Boolean result = friendService.cancelFriends(1L, 2L);
        assertEquals(true, friendRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 2L, FriendState.CANCEL));
        assertEquals(true, result);
    }

    /**
     * 친구 목록 요청 테스트
     */
    @Test
    void friendsListTest() {
        /* 정상적인 요청 */
        friendService.requestFriends(1L, 2L);
        friendService.requestFriends(3L, 1L);
        friendService.requestFriends(4L, 1L);
        friendService.requestFriends(4L, 2L);
        friendService.acceptFriendsRequest(1L, 2L);
        friendService.acceptFriendsRequest(3L, 1L);
        friendService.acceptFriendsRequest(4L, 1L);
        friendService.acceptFriendsRequest(4L, 2L);
        assertEquals(3, friendService.findFriendsByMemberId(1L, FriendState.ACCEPT).size());
        assertEquals(2, friendService.findFriendsByMemberId(2L, FriendState.ACCEPT).size());
        assertEquals(1, friendService.findFriendsByMemberId(3L, FriendState.ACCEPT).size());
        assertEquals(2, friendService.findFriendsByMemberId(4L, FriendState.ACCEPT).size());

        /* 친구가 계정을 비활성화할 때 */
        memberService.disableMember(2L); // 비활성화
        assertEquals(2, friendService.findFriendsByMemberId(1L, FriendState.ACCEPT).size());
        ApiException thrown = assertThrows(ApiException.class, ()-> friendService.findFriendsByMemberId(2L, FriendState.ACCEPT));
        assertEquals("M0001", thrown.getErrorCode());
        assertEquals(1, friendService.findFriendsByMemberId(3L, FriendState.ACCEPT).size());
        assertEquals(1, friendService.findFriendsByMemberId(4L, FriendState.ACCEPT).size());
    }

    /**
     * 친구 목록 요청 테스트 (내가 보낸 친구 요청)
     */
    @Test
    void friendsListSentTest() {
        /* 정상적인 요청 */
        friendService.requestFriends(1L, 2L);
        friendService.requestFriends(1L, 3L);
        friendService.requestFriends(1L, 4L);
        friendService.requestFriends(1L, 5L);
        assertEquals(4, friendService.findFriendsByMemberId(1L, FriendState.REQUEST).size());

        /* 존재하지 않은 유저의 요청 */
        ApiException thrown = assertThrows(ApiException.class, () -> friendService.findFriendsByMemberId(-1L, FriendState.REQUEST));
        assertEquals("M0001", thrown.getErrorCode());
    }

    /**
     * 친구 목록 요청 테스트 (내가 받은 친구 요청)
     */
    @Test
    void friendsListReceivedTest() {
        /* 정상적인 요청 */
        friendService.requestFriends(3L, 1L);
        friendService.requestFriends(1L, 2L);
        friendService.requestFriends(4L, 2L);
        assertEquals(1, friendService.findByTargetMemberIdAndFriendState(1L, FriendState.REQUEST).size());
        assertEquals(2, friendService.findByTargetMemberIdAndFriendState(2L, FriendState.REQUEST).size());

        /* 존재하지 않은 유저의 요청 */
        ApiException thrown = assertThrows(ApiException.class, () -> friendService.findByTargetMemberIdAndFriendState(-1L, FriendState.REQUEST));
        assertEquals("M0001", thrown.getErrorCode());
    }
}