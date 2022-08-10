package sprint.server.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.exception.ApiException;
import sprint.server.domain.friends.FriendState;
import sprint.server.domain.friends.Friends;
import sprint.server.repository.FriendsRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class FriendsServiceTest {

    @Autowired FriendsService friendsService;
    @Autowired FriendsRepository friendsRepository;

    /**
     * 친구 요청 테스트
     */
    @Test
    public void friendsRequest() throws Exception{
        /* 정상적인 요청 */
        Friends friends = friendsService.FriendsRequest(1L, 2L);
        assertEquals(friends, friendsRepository.findById(friends.getId()).get());

        /* 이미 해당 친구 추가 요청이 존재할 경우*/
        ApiException thrown = assertThrows(ApiException.class, () -> friendsService.FriendsRequest(1L, 2L));
        assertEquals("F0002", thrown.getErrorCode());

        /* 이미 둘이 친구인 경우*/
        friendsService.AcceptFriendsRequest(1L, 2L);
        ApiException thrown2 = assertThrows(ApiException.class, () -> friendsService.FriendsRequest(1L, 2L));
        assertEquals("F0003", thrown2.getErrorCode());

        /** 해당 유저가 존재하지 않은 경우
         *  추후 추가 예정
         */
    }

    /**
     * 친구 추가 거절 요청 테스트
     */
    @Test
    public void rejectFriendsRequest() {
        /* 해당 친구 추가 요청이 존재하지 없을 때 */
        ApiException thrown = assertThrows(ApiException.class, () -> friendsService.RejectFriendsRequest(1L, 2L));
        assertEquals("F0001", thrown.getErrorCode());

        /* 정상적인 요청 */
        Friends friends = friendsService.FriendsRequest(1L, 2L);
        Boolean result = friendsService.RejectFriendsRequest(1L, 2L);
        assertEquals(FriendState.REJECT ,friends.getEstablishState());
        assertEquals(true, result);
    }

    /**
     * 친구 추가 수락 요청 테스트
     */
    @Test
    public void acceptFriendsRequest() {
        /* 해당 친구 추가 요청이 존재하지 없을 때 */
        ApiException thrown = assertThrows(ApiException.class, () -> friendsService.AcceptFriendsRequest(1L, 2L));
        assertEquals("F0001", thrown.getErrorCode());

        /* 정상적인 요청 */
        Friends friends = friendsService.FriendsRequest(1L, 2L);
        Boolean result = friendsService.AcceptFriendsRequest(1L, 2L);
        assertEquals(FriendState.ACCEPT, friends.getEstablishState());
        assertEquals(true, friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 2L, FriendState.ACCEPT));
        assertEquals(true, friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(2L, 1L, FriendState.ACCEPT));
        assertEquals(true, result);

        /* 이미 둘이 친구인 경우 */
        ApiException thrown2 = assertThrows(ApiException.class, () -> friendsService.AcceptFriendsRequest(1L, 2L));
        assertEquals("F0003", thrown2.getErrorCode());
    }

    /**
     * 친구 제거 요청 테스트
     */
    @Test
    public void deleteFriends() {
        /* 둘이 친구 관계가 아닌 경우*/
        ApiException thrown = assertThrows(ApiException.class, () -> friendsService.DeleteFriends(1L, 2L));
        assertEquals("F0004", thrown.getErrorCode());

        /* 정상적인 요청 */
        friendsService.FriendsRequest(1L, 2L);
        friendsService.AcceptFriendsRequest(1L, 2L);
        friendsService.DeleteFriends(1L, 2L);
        assertEquals(true, friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 2L, FriendState.REJECT));
        assertEquals(true, friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(2L, 1L, FriendState.REJECT));
        assertEquals(false, friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 2L, FriendState.ACCEPT));
        assertEquals(false, friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(2L, 1L, FriendState.ACCEPT));
    }

    @Test
    public void cancelFriends() {
        /* 해당 친구 추가 요청이 존재하지 없을 때 */
        ApiException thrown = assertThrows(ApiException.class, () -> friendsService.CancelFriends(1L, 2L));
        assertEquals("F0001", thrown.getErrorCode());

        /* 정상적인 요청 */
        friendsService.FriendsRequest(1L, 2L);
        Boolean result = friendsService.CancelFriends(1L, 2L);
        assertEquals(true, friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(1L, 2L, FriendState.CANCELED));
        assertEquals(true, result);

    }

    /**
     * 친구 목록 요청 테스트
     */
    @Test
    public void friendsList() {
        friendsService.FriendsRequest(1L, 2L);
        friendsService.FriendsRequest(3L, 1L);
        friendsService.FriendsRequest(4L, 1L);
        friendsService.FriendsRequest(4L, 2L);
        friendsService.AcceptFriendsRequest(1L, 2L);
        friendsService.AcceptFriendsRequest(3L, 1L);
        friendsService.AcceptFriendsRequest(4L, 1L);
        friendsService.AcceptFriendsRequest(4L, 2L);
        assertEquals(3, friendsService.LoadFriendsBySourceMember(1L, FriendState.ACCEPT).size());
        assertEquals(2, friendsService.LoadFriendsBySourceMember(2L, FriendState.ACCEPT).size());
        assertEquals(1, friendsService.LoadFriendsBySourceMember(3L, FriendState.ACCEPT).size());
    }

    /**
     * 친구 목록 요청 테스트 (내가 보낸 친구 요청)
     */
    @Test
    public void friendsListSent() {
        friendsService.FriendsRequest(1L, 2L);
        friendsService.FriendsRequest(1L, 3L);
        friendsService.FriendsRequest(1L, 4L);
        friendsService.FriendsRequest(1L, 5L);
        assertEquals(4, friendsService.LoadFriendsBySourceMember(1L, FriendState.REQUEST).size());
    }

    @Test
    public void friendsListReceived() {
        friendsService.FriendsRequest(3L, 1L);
        friendsService.FriendsRequest(1L, 2L);
        friendsService.FriendsRequest(4L, 2L);
        assertEquals(1, friendsService.LoadFriendsByTargetMember(1L, FriendState.REQUEST).size());
        assertEquals(2, friendsService.LoadFriendsByTargetMember(2L, FriendState.REQUEST).size());
    }
}