package sprint.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.member.Member;
import sprint.server.domain.friends.FriendState;
import sprint.server.domain.friends.Friends;
import sprint.server.repository.FriendsRepository;
import sprint.server.repository.MemberRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendsService {
    private final FriendsRepository friendsRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    /**
     * 친구 요청
     * @param sourceMemberId -> 친구요청을 보내는 UserId
     * @param targetMemberId -> 친구요청을 받는 UserId
     * @return friends
     */
    @Transactional
    public Boolean requestFriends(Long sourceMemberId, Long targetMemberId) {
        validationFriendsRequest(sourceMemberId, targetMemberId);
        /* 만약 이미 targetMemberId가 sourceMemberId에게 친구요청을 보낸적이 있다면 */
        if (friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(targetMemberId, sourceMemberId, FriendState.REQUEST)) {
            return acceptFriendsRequest(targetMemberId, sourceMemberId);
        } else {
            Friends friends = new Friends(sourceMemberId, targetMemberId);
            friends.setRegisteredDate(Timestamp.valueOf(LocalDateTime.now()));
            friendsRepository.save(friends);
            return friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.REQUEST);
        }
    }


    /**
     * 친구 요청 거절
     * @param sourceMemberId -> 친구요청을 거절하는 UserId
     * @param targetMemberId -> 친구요청을 보낸 UserId
     * @return 결과(true/false)
     */
    @Transactional
    public Boolean rejectFriendsRequest(Long sourceMemberId, Long targetMemberId){
        Optional<Friends> friends = findFriendsRequest(sourceMemberId, targetMemberId, FriendState.REQUEST);
        if (friends.isEmpty()){
            throw new ApiException(ExceptionEnum.FRIENDS_REQUEST_NOT_FOUND);
        }
        friends.get().setRegisteredDate(Timestamp.valueOf(LocalDateTime.now()));
        friends.get().setEstablishState(FriendState.REJECT);
        return friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.REJECT);
    }

    /**
     * 친구 요청 수락
     * @param sourceMemberId -> 친구요청을 수락하는 UserId
     * @param targetMemberId -> 친구요청을 요청한 UserId
     * @return 결과(true/false)
     */
    @Transactional
    public Boolean acceptFriendsRequest(Long sourceMemberId, Long targetMemberId){
        /* 이미 친구인 경우 */
        if (isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.ACCEPT)) {
            throw new ApiException(ExceptionEnum.FRIENDS_ALREADY_FRIEND);
        }
        Optional<Friends> friends = friendsRepository.findBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.REQUEST);
        /* 해당 친구요청이 존재하지 않은 경우 */
        if (friends.isEmpty()) {
            throw new ApiException(ExceptionEnum.FRIENDS_REQUEST_NOT_FOUND);
        }
        friends.get().setEstablishState(FriendState.ACCEPT);
        friends.get().setRegisteredDate(Timestamp.valueOf(LocalDateTime.now()));

        return isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.ACCEPT);
    }

    /**
     * 친구 제거
     * @param sourceMemberId -> 친구 제거를 요청한 UserId
     * @param targetMemberId -> 친구 목록에서 삭제되길 기대되는 UserId
     * @return 결과(true/false)
     */
    @Transactional
    public Boolean deleteFriends(Long sourceMemberId, Long targetMemberId) {
        Optional<Friends> friends = findFriendsRequest(sourceMemberId, targetMemberId, FriendState.ACCEPT);
        /* 해당 친구관계가 존재하지 않은 경우 */
        if(friends.isEmpty()) {
            throw new ApiException(ExceptionEnum.FRIENDS_NOT_FOUND);
        }
        friends.get().setEstablishState(FriendState.DELETED);
        friends.get().setRegisteredDate(Timestamp.valueOf(LocalDateTime.now()));

        return isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.DELETED);
    }


    /**
     * 친구 요청 취소
     * @param sourceMemberId -> 친구요청 취소를 요청한 UserId
     * @param targetMemberId -> 친구요청 대상 UserId
     */
    @Transactional
    public Boolean cancelFriends(Long sourceMemberId, Long targetMemberId) {
        Optional<Friends> friends = friendsRepository.findBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.REQUEST);
        /* 해당 친구요청이 존재하지 않은 경우*/
        if (friends.isEmpty()) {
            throw new ApiException(ExceptionEnum.FRIENDS_REQUEST_NOT_FOUND);
        }
        friends.get().setRegisteredDate(Timestamp.valueOf(LocalDateTime.now()));
        friends.get().setEstablishState(FriendState.CANCELED);

        return friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.CANCELED);
    }

    /**
     * 친구 요청 Validation
     * @param sourceMemberId -> 친구요청을 보내는 사람
     * @param targetMemberId -> 친구요청을 받는 사람
     */
    private void validationFriendsRequest(Long sourceMemberId, Long targetMemberId){
        if (!memberService.existById(sourceMemberId)) {
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND, "Source Member가 존재하지 않습니다.");
        } else if (!memberService.existById(targetMemberId)) {
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND, "Target Member가 존재하지 않습니다.");
        } else if (isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.ACCEPT)) {
            throw new ApiException(ExceptionEnum.FRIENDS_ALREADY_FRIEND);
        } else if (friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.REQUEST)){
            throw new ApiException(ExceptionEnum.FRIENDS_ALREADY_SENT);
        }
    }

    private boolean isFriendsRequestExist(Long sourceMemberId, Long targetMemberId, FriendState friendState) {
        return findFriendsRequest(sourceMemberId, targetMemberId, friendState).isPresent();
    }

    private Optional<Friends> findFriendsRequest(Long sourceMemberId, Long targetMemberId, FriendState friendState){
        return findByTwoMemberIdAndEstablishState(sourceMemberId, targetMemberId, friendState);
    }

    public Optional<Friends> findByTwoMemberIdAndEstablishState(Long sourceMemberId, Long targetMemberId, FriendState friendState) {
        Optional<Friends> friends = friendsRepository.findBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, friendState);
        if (friends.isPresent()) {
            return friends;
        } else {
            return friendsRepository.findBySourceMemberIdAndTargetMemberIdAndEstablishState(targetMemberId, sourceMemberId, friendState);
        }
    }

    private List<Friends> findByMemberIdAndEstablishState(Long memberId, FriendState friendState) {
        List<Friends> friendsList1 = friendsRepository.findBySourceMemberIdAndEstablishState(memberId, friendState);
        List<Friends> friendsList2 = friendsRepository.findByTargetMemberIdAndEstablishState(memberId, friendState);
        return Stream.concat(friendsList1.stream(), friendsList2.stream()).collect(Collectors.toList());
    }

    public List<Member> findFriendsByMemberId(Long memberId, FriendState friendState) {
        if (!memberService.existById(memberId)){
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        }
        List<Friends> friendsList = findByMemberIdAndEstablishState(memberId, friendState);
        return friendsList.stream()
                .filter(friends -> memberRepository.findById(friends.getTargetMemberId()).get().getDisableDay() == null
                        && memberRepository.findById(friends.getSourceMemberId()).get().getDisableDay() == null)
                .map(friends -> findMemberInFriend(friends, memberId))
                .collect(Collectors.toList());
    }

    private Member findMemberInFriend(Friends friends, Long memberId) {
        if (friends.getSourceMemberId().equals(memberId)) {
            return memberService.findById(friends.getTargetMemberId());
        } else {
            return memberService.findById(friends.getSourceMemberId());
        }
    }

    public List<Member> findBySourceMemberIdAndFriendState(Long memberId, FriendState friendState) {
        if (!memberService.existById(memberId)){
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        }
        List<Friends> friendsList = friendsRepository.findBySourceMemberIdAndEstablishState(memberId, friendState);
        return friendsList.stream()
                .filter(friends -> memberRepository.findById(friends.getTargetMemberId()).get().getDisableDay() == null)
                .map(friends -> memberService.findById(friends.getTargetMemberId()))
                .collect(Collectors.toList());
    }
    public List<Member> findByTargetMemberIdAndFriendState(Long memberId, FriendState friendState) {
        if (!memberService.existById(memberId)){
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        }
        List<Friends> friendsList = friendsRepository.findByTargetMemberIdAndEstablishState(memberId, friendState);
        return friendsList.stream()
                .filter(friends -> memberRepository.findById(friends.getSourceMemberId()).get().getDisableDay() == null)
                .map(friends -> memberService.findById(friends.getSourceMemberId())).
                collect(Collectors.toList());
    }
}
