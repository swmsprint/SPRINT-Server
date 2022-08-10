package sprint.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.Member;
import sprint.server.domain.friends.FriendState;
import sprint.server.domain.friends.Friends;
import sprint.server.repository.FriendsRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendsService {
    private final FriendsRepository friendsRepository;
    private final MemberService memberService;

    /**
     * 친구 요청
     * @param sourceMemberId -> 친구요청을 보내는 UserId
     * @param targetMemberId -> 친구요청을 받는 UserId
     * @return friends
     */
    @Transactional
    public Friends FriendsRequest(Long sourceMemberId, Long targetMemberId) {
        validationFriendsRequest(sourceMemberId, targetMemberId);
        Friends friends = Friends.createFriendsRelationship(sourceMemberId, targetMemberId);
        friends.setRegisteredDate(Timestamp.valueOf(LocalDateTime.now()));
        friendsRepository.save(friends);
        return friends;
    }


    /**
     * 친구 요청 거절
     * @param sourceMemberId -> 친구요청을 거절하는 UserId
     * @param targetMemberId -> 친구요청을 보낸 UserId
     * @return 결과(true/false)
     */
    @Transactional
    public Boolean RejectFriendsRequest(Long sourceMemberId, Long targetMemberId){
        boolean isExists = isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.REQUEST);
        if (!isExists) {
            throw new ApiException(ExceptionEnum.FRIENDS_REQUEST_NOT_FOUND);
        }
        Friends friends = findFriendsRequest(sourceMemberId, targetMemberId, FriendState.REQUEST).get();
        setFriendsByStateAndTime(friends, FriendState.REJECT);
        if (friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.REJECT)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 친구 요청 수락
     * @param sourceMemberId -> 친구요청을 수락하는 UserId
     * @param targetMemberId -> 친구요청을 요청한 UserId
     * @return 결과(true/false)
     */
    @Transactional
    public Boolean AcceptFriendsRequest(Long sourceMemberId, Long targetMemberId){
        boolean isExists = isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.REQUEST);
        if (!isExists) {
            throw new ApiException(ExceptionEnum.FRIENDS_REQUEST_NOT_FOUND);
        }
        boolean isExists2 = isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.ACCEPT);
        if (isExists2) {
            throw new ApiException(ExceptionEnum.FRIENDS_ALREADY_FRIEND);
        }
        Friends friends = findFriendsRequest(sourceMemberId, targetMemberId, FriendState.REQUEST).get();
        setFriendsByStateAndTime(friends, FriendState.ACCEPT);
        Friends newFriends = Friends.createFriendsRelationship(targetMemberId, sourceMemberId);
        setFriendsByStateAndTime(newFriends, FriendState.ACCEPT);

        friendsRepository.save(newFriends);
        if (isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.ACCEPT) &&
                isFriendsRequestExist(targetMemberId, sourceMemberId, FriendState.ACCEPT)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 친구 제거
     * @param sourceMemberId -> 친구 제거를 요청한 UserId
     * @param targetMemberId -> 친구 목록에서 삭제되길 기대되는 UserId
     * @return 결과(true/false)
     */
    @Transactional
    public Boolean DeleteFriends(Long sourceMemberId, Long targetMemberId) {
        Optional<Friends> sourceFriends = findFriendsRequest(sourceMemberId, targetMemberId, FriendState.ACCEPT);
        Optional<Friends> targetFriends = findFriendsRequest(targetMemberId, sourceMemberId, FriendState.ACCEPT);
        if(!sourceFriends.isPresent() || !targetFriends.isPresent()) {
            throw new ApiException(ExceptionEnum.FRIENDS_NOT_FRIEND);
        }
        setFriendsByStateAndTime(sourceFriends.get(), FriendState.REJECT);
        setFriendsByStateAndTime(targetFriends.get(), FriendState.REJECT);

        if (isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.REJECT) &&
                isFriendsRequestExist(targetMemberId, sourceMemberId, FriendState.REJECT)){
            return true;
        } else {
            return false;
        }
    }


    /**
     * 친구 요청 취소
     * @param sourceMemberId -> 친구요청 취소를 요청한 UserId
     * @param targetMemberId -> 친구요청 대상 UserId
     * @return
     */
    @Transactional
    public Boolean CancelFriends(Long sourceMemberId, Long targetMemberId) {
        Optional<Friends> sourceFriends = findFriendsRequest(sourceMemberId, targetMemberId, FriendState.REQUEST);
        if (!sourceFriends.isPresent()) {
            throw new ApiException(ExceptionEnum.FRIENDS_REQUEST_NOT_FOUND);
        }
        setFriendsByStateAndTime(sourceFriends.get(), FriendState.CANCELED);

        if (isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.CANCELED)) {
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
            throw new ApiException(ExceptionEnum.FRIENDS_ALREADY_SENT);
        } else if (isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.ACCEPT)) {
            throw new ApiException(ExceptionEnum.FRIENDS_ALREADY_FRIEND);
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

    private void setFriendsByStateAndTime(Friends friends, FriendState friendState) {
        friends.setRegisteredDate(Timestamp.valueOf(LocalDateTime.now()));
        friends.setEstablishState(friendState);
    }

    public List<Member> LoadFriendsBySourceMember(Long memberId, FriendState friendState) {
        List<Friends> friendsList = friendsRepository.findBySourceMemberIdAndEstablishState(memberId, friendState);
        List<Member> result = friendsList.stream().map(friends -> memberService.findById(friends.getTargetMemberId())).collect(Collectors.toList());
        return result;
    }

    public List<Member> LoadFriendsByTargetMember(Long memberId, FriendState friendState) {
        List<Friends> friendsList = friendsRepository.findByTargetMemberIdAndEstablishState(memberId, friendState);
        List<Member> result = friendsList.stream().map(friends -> memberService.findById(friends.getSourceMemberId())).collect(Collectors.toList());
        return result;
    }
}
