package sprint.server.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.exception.ApiException;
import sprint.server.domain.friends.FriendState;
import sprint.server.domain.friends.Friends;
import sprint.server.repository.FriendsRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class FriendsServiceTest {

    @Autowired FriendsService friendsService;
    @Autowired FriendsRepository friendsRepository;
    @Autowired MemberService memberService;

    @Test
    public void Test() {
        friendsService.requestFriends(1L, 2L);
        Optional<Friends> friends1 = friendsService.findByTwoMemberIdAndEstablishState(2L, 1L, FriendState.REQUEST);
        if (friends1.isPresent()){
            assertEquals(1L, friends1.get().getSourceMemberId());
            assertEquals(2L, friends1.get().getTargetMemberId());
        }
        assertEquals(friends1, friendsService.findByTwoMemberIdAndEstablishState(2L, 1L, FriendState.REQUEST));
    }
    /**
     * 친구 요청 테스트
     */
    @Test
    public void friendsRequestTest(){
        /* 정상적인 요청 */
        Boolean requestResult = friendsService.requestFriends(1L, 2L);
        Friends friends1 = friendsRepository.findBySourceMemberIdAndTargetMemberId(1L, 2L).get();
        assertEquals(true, requestResult);
        assertEquals(1L, friends1.getSourceMemberId());
        assertEquals(2L, friends1.getTargetMemberId());
        assertEquals(FriendState.REQUEST, friends1.getEstablishState());

        /* 이미 해당 친구 추가 요청이 존재할 경우*/
        ApiException thrown = assertThrows(ApiException.class, () -> friendsService.requestFriends(1L, 2L));
        assertEquals("F0002", thrown.getErrorCode());

        /* 이미 둘이 친구인 경우*/
        friendsService.acceptFriendsRequest(1L, 2L);
        ApiException thrown2 = assertThrows(ApiException.class, () -> friendsService.requestFriends(1L, 2L));
        assertEquals("F0003", thrown2.getErrorCode());

        /* 이미 상대방으로부터 친구 추가 요청이 존재할 경우 */
        Boolean requestResult2 = friendsService.requestFriends(1L, 3L);
        Boolean requestResult3 = friendsService.requestFriends(3L, 1L);
        Friends friends2 = friendsRepository.findBySourceMemberIdAndTargetMemberId(1L, 3L).get();
        assertEquals(1L, friends2.getSourceMemberId());
        assertEquals(3L, friends2.getTargetMemberId());
        assertEquals(FriendState.ACCEPT, friends2.getEstablishState());
        assertEquals(true, requestResult2);
        assertEquals(true, requestResult3);
        assertEquals(true, friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 3L, FriendState.ACCEPT));

        /* 해당 유저가 존재하지 않은 경우 */
        ApiException thrown3 = assertThrows(ApiException.class, () -> friendsService.requestFriends(-1L, 1L));
        assertEquals("Source Member가 존재하지 않습니다.", thrown3.getMessage());
        ApiException thrown4 = assertThrows(ApiException.class, () -> friendsService.requestFriends(1L, -1L));
        assertEquals("Target Member가 존재하지 않습니다.", thrown4.getMessage());
    }

    /**
     * 친구 추가 거절 요청 테스트
     */
    @Test
    public void rejectFriendsRequestTest() {
        /* 해당 친구 추가 요청이 존재하지 없을 때 */
        ApiException thrown = assertThrows(ApiException.class, () -> friendsService.rejectFriendsRequest(1L, 2L));
        assertEquals("F0001", thrown.getErrorCode());

        /* 정상적인 요청 */
        Boolean result1 = friendsService.requestFriends(1L, 2L);
        Boolean result2 = friendsService.rejectFriendsRequest(1L, 2L);
        Friends friends = friendsRepository.findBySourceMemberIdAndTargetMemberId(1L, 2L).get();
        assertEquals(1L, friends.getSourceMemberId());
        assertEquals(2L, friends.getTargetMemberId());
        assertEquals(FriendState.REJECT ,friends.getEstablishState());
        assertEquals(true, result1);
        assertEquals(true, result2);
    }

    /**
     * 친구 추가 수락 요청 테스트
     */
    @Test
    public void acceptFriendsRequestTest() {
        /* 해당 친구 추가 요청이 존재하지 없을 때 */
        ApiException thrown = assertThrows(ApiException.class, () -> friendsService.acceptFriendsRequest(1L, 2L));
        assertEquals("F0001", thrown.getErrorCode());

        /* 정상적인 요청 */
        Boolean result1 = friendsService.requestFriends(1L, 2L);
        Boolean result2 = friendsService.acceptFriendsRequest(1L, 2L);
        Friends friends = friendsRepository.findBySourceMemberIdAndTargetMemberId(1L, 2L).get();
        assertEquals(FriendState.ACCEPT, friends.getEstablishState());
        assertEquals(true, friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 2L, FriendState.ACCEPT));
        assertEquals(true, result1);
        assertEquals(true, result2);

        /* 이미 둘이 친구인 경우 */
        ApiException thrown2 = assertThrows(ApiException.class, () -> friendsService.acceptFriendsRequest(1L, 2L));
        assertEquals("F0003", thrown2.getErrorCode());
    }

    /**
     * 친구 제거 요청 테스트
     */
    @Test
    public void deleteFriendsTest() {
        /* 둘이 친구 관계가 아닌 경우*/
        ApiException thrown = assertThrows(ApiException.class, () -> friendsService.deleteFriends(1L, 2L));
        assertEquals("F0004", thrown.getErrorCode());

        /* 정상적인 요청 */
        friendsService.requestFriends(1L, 2L);
        friendsService.acceptFriendsRequest(1L, 2L);
        friendsService.deleteFriends(1L, 2L);
        assertEquals(true, friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 2L, FriendState.DELETE));
        assertEquals(false, friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 2L, FriendState.ACCEPT));
    }

    @Test
    public void cancelFriendsTest() {
        /* 해당 친구 추가 요청이 존재하지 없을 때 */
        ApiException thrown = assertThrows(ApiException.class, () -> friendsService.cancelFriends(1L, 2L));
        assertEquals("F0001", thrown.getErrorCode());

        /* 정상적인 요청 */
        friendsService.requestFriends(1L, 2L);
        Boolean result = friendsService.cancelFriends(1L, 2L);
        assertEquals(true, friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 2L, FriendState.CANCEL));
        assertEquals(true, result);
    }

    /**
     * 친구 목록 요청 테스트
     */
    @Test
    public void friendsListTest() {
        /* 정상적인 요청 */
        friendsService.requestFriends(1L, 2L);
        friendsService.requestFriends(3L, 1L);
        friendsService.requestFriends(4L, 1L);
        friendsService.requestFriends(4L, 2L);
        friendsService.acceptFriendsRequest(1L, 2L);
        friendsService.acceptFriendsRequest(3L, 1L);
        friendsService.acceptFriendsRequest(4L, 1L);
        friendsService.acceptFriendsRequest(4L, 2L);
        assertEquals(3, friendsService.findFriendsByMemberId(1L, FriendState.ACCEPT).size());
        assertEquals(2, friendsService.findFriendsByMemberId(2L, FriendState.ACCEPT).size());
        assertEquals(1, friendsService.findFriendsByMemberId(3L, FriendState.ACCEPT).size());
        assertEquals(2, friendsService.findFriendsByMemberId(4L, FriendState.ACCEPT).size());

        /* 친구가 계정을 비활성화할 때 */
        memberService.disableMember(2L); // 비활성화
        assertEquals(2, friendsService.findFriendsByMemberId(1L, FriendState.ACCEPT).size());
        ApiException thrown = assertThrows(ApiException.class, ()-> friendsService.findFriendsByMemberId(2L, FriendState.ACCEPT));
        assertEquals("M0001", thrown.getErrorCode());
        assertEquals(1, friendsService.findFriendsByMemberId(3L, FriendState.ACCEPT).size());
        assertEquals(1, friendsService.findFriendsByMemberId(4L, FriendState.ACCEPT).size());
    }

    /**
     * 친구 목록 요청 테스트 (내가 보낸 친구 요청)
     */
    @Test
    public void friendsListSentTest() {
        /* 정상적인 요청 */
        friendsService.requestFriends(1L, 2L);
        friendsService.requestFriends(1L, 3L);
        friendsService.requestFriends(1L, 4L);
        friendsService.requestFriends(1L, 5L);
        assertEquals(4, friendsService.findFriendsByMemberId(1L, FriendState.REQUEST).size());

        /* 존재하지 않은 유저의 요청 */
        ApiException thrown = assertThrows(ApiException.class, () ->friendsService.findFriendsByMemberId(-1L, FriendState.REQUEST));
        assertEquals("M0001", thrown.getErrorCode());
    }

    /**
     * 친구 목록 요청 테스트 (내가 받은 친구 요청)
     */
    @Test
    public void friendsListReceivedTest() {
        /* 정상적인 요청 */
        friendsService.requestFriends(3L, 1L);
        friendsService.requestFriends(1L, 2L);
        friendsService.requestFriends(4L, 2L);
        assertEquals(1, friendsService.findByTargetMemberIdAndFriendState(1L, FriendState.REQUEST).size());
        assertEquals(2, friendsService.findByTargetMemberIdAndFriendState(2L, FriendState.REQUEST).size());

        /* 존재하지 않은 유저의 요청 */
        ApiException thrown = assertThrows(ApiException.class, () ->friendsService.findByTargetMemberIdAndFriendState(-1L, FriendState.REQUEST));
        assertEquals("M0001", thrown.getErrorCode());
    }
}