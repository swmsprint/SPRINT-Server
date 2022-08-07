package sprint.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.friends.FriendState;
import sprint.server.domain.friends.Friends;
import sprint.server.repository.FriendsRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendsService {
    private final FriendsRepository friendsRepository;
    private final MemberService memberService;

    /**
     * 친구 요청
     * @param sourceMemberId
     * @param targetMemberId
     * @return friends
     */
    @Transactional
    public Friends FriendsRequest(Long sourceMemberId, Long targetMemberId) {
        validationFriendsRequest(sourceMemberId, targetMemberId);
        Friends friends = Friends.createFriendsRelationship(sourceMemberId, targetMemberId);
        friendsRepository.save(friends);
        return friends;
    }


    /**
     * 친구 요청 거절
     * @param sourceMemberId, targetMemberId
     * @return 결과(true/false)
     */
    @Transactional
    public Boolean RejectFriendsRequest(Long sourceMemberId, Long targetMemberId){
        boolean isExists = isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.REQUEST);
        if (!isExists) {
            throw new IllegalStateException("해당 친구 요청이 존재하지 않습니다.");
        }
        Friends friends = findFriendsRequest(sourceMemberId, targetMemberId, FriendState.REQUEST).get();
        friends.setEstablishState(FriendState.REJECT);
        if (friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.REJECT)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 친구 요청 수락
     * @param sourceMemberId, targetMemberId
     * @return 결과(true/false)
     */
    @Transactional
    public Boolean AcceptFriendsRequest(Long sourceMemberId, Long targetMemberId){
        boolean isExists = isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.REQUEST);
        if (!isExists) {
            throw new IllegalStateException("해당 친구 요청이 존재하지 않습니다.");
        }
        Friends friends = findFriendsRequest(sourceMemberId, targetMemberId, FriendState.REQUEST).get();
        friends.setEstablishState(FriendState.ACCEPT);
        Friends newFriends = Friends.createFriendsRelationship(targetMemberId, sourceMemberId);
        newFriends.setEstablishState(FriendState.ACCEPT);
        friendsRepository.save(newFriends);
        if (isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.ACCEPT) &&
                isFriendsRequestExist(targetMemberId, sourceMemberId, FriendState.ACCEPT)) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public Boolean DeleteFriends(Long sourceMemberId, Long targetMemberId) {
        Optional<Friends> sourceFriends = findFriendsRequest(sourceMemberId, targetMemberId, FriendState.ACCEPT);
        Optional<Friends> targetFriends = findFriendsRequest(targetMemberId, sourceMemberId, FriendState.ACCEPT);
        if(!sourceFriends.isPresent() || !targetFriends.isPresent()) {
            throw new IllegalStateException("잘못된 요청입니다. : 친구가 아닙니다.");
        }
        sourceFriends.get().setEstablishState(FriendState.REJECT);
        targetFriends.get().setEstablishState(FriendState.REJECT);
        if (isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.REJECT) &&
                isFriendsRequestExist(targetMemberId, sourceMemberId, FriendState.REJECT)){
            return true;
        } else {
            return false;
        }
    }


    /**
     * 친구 요청 Validation
     * @param sourceMemberId
     * @param targetMemberId
     */
    private void validationFriendsRequest(Long sourceMemberId, Long targetMemberId){
        memberService.isMemberExistById(sourceMemberId, "sourceMember가 database에 존재하지 않습니다.");
        memberService.isMemberExistById(targetMemberId, "targetMember가 database에 존재하지 않습니다.");
        if (isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.REQUEST)){
            throw new IllegalStateException("이미 전송된 요청입니다.");
        } else if (isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.ACCEPT)) {
            throw new IllegalStateException("이미 친구입니다.");
        }
    }

    /**
     * 친구 요청 응답 전 Validation
     * @param sourceMemberId
     * @param targetMemberId
     */
    private boolean isFriendsRequestExist(Long sourceMemberId, Long targetMemberId, FriendState friendState) {
        Boolean isExists = friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, friendState);
        return isExists;
    }

    private Optional<Friends> findFriendsRequest(Long sourceMemberId, Long targetMemberId, FriendState friendState){
        return friendsRepository.findBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, friendState);
    }

}
