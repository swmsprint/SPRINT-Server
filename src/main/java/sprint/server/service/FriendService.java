package sprint.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.exception.ApiException;
import sprint.server.controller.exception.ExceptionEnum;
import sprint.server.domain.member.Member;
import sprint.server.domain.friend.FriendState;
import sprint.server.domain.friend.Friend;
import sprint.server.repository.FriendRepository;
import sprint.server.repository.MemberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {
    private final FriendRepository friendRepository;
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
        /* sourceMemberId, targetMemberId의 존재여부, 이미 친구관계인지 여부, 해당 요청이 이미 전송되었는지 여부를 검사 */
        validationFriendsRequest(sourceMemberId, targetMemberId);

        /* 만약 이미 targetMemberId가 sourceMemberId에게 친구요청을 보낸적이 있다면 */
        if (friendRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(targetMemberId, sourceMemberId, FriendState.REQUEST)) {
            return acceptFriendsRequest(targetMemberId, sourceMemberId);
        }
        Optional<Friend> exists_friend = friendRepository.findBySourceMemberIdAndTargetMemberId(targetMemberId, sourceMemberId);
        if (exists_friend.isPresent()) {
            /* 만약 이미 DELETE, REJECT, CANCEL 관계라면 기존 관계 삭제*/
            friendRepository.deleteBySourceMemberIdAndTargetMemberId(targetMemberId, sourceMemberId);
        }

        /* 새로운 관계 생성 */
        Friend newFriend = new Friend(sourceMemberId, targetMemberId);
        friendRepository.save(newFriend);
        return friendRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.REQUEST);
    }


    /**
     * 친구 요청 거절
     * @param sourceMemberId -> 친구요청을 거절하는 UserId
     * @param targetMemberId -> 친구요청을 보낸 UserId
     * @return 결과(true/false)
     */
    @Transactional
    public Boolean rejectFriendsRequest(Long sourceMemberId, Long targetMemberId){
        Optional<Friend> friends = friendRepository.findBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.REQUEST);
        if (friends.isEmpty()){
            throw new ApiException(ExceptionEnum.FRIENDS_REQUEST_NOT_FOUND);
        }
        friends.get().setEstablishState(FriendState.REJECT);
        return friendRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.REJECT);
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
        Optional<Friend> friends = friendRepository.findBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.REQUEST);
        /* 해당 친구요청이 존재하지 않은 경우 */
        if (friends.isEmpty()) {
            throw new ApiException(ExceptionEnum.FRIENDS_REQUEST_NOT_FOUND);
        }
        friends.get().setEstablishState(FriendState.ACCEPT);

        return isFriendsRequestExist(sourceMemberId, targetMemberId, FriendState.ACCEPT);
    }

    /**
     * 친구 제거
     * @param sourceMember -> 친구 제거를 요청한 User
     * @param targetMember -> 친구 목록에서 삭제되길 기대되는 User
     * @return 결과(true/false)
     */
    @Transactional
    public Boolean deleteFriends(Member sourceMember, Member targetMember) {
        Optional<Friend> friends = findByTwoMemberIdAndEstablishState(sourceMember.getId(), targetMember.getId(), FriendState.ACCEPT);
        /* 해당 친구관계가 존재하지 않은 경우 */
        if(friends.isEmpty()) {
            throw new ApiException(ExceptionEnum.FRIENDS_NOT_FOUND);
        }
        friends.get().setEstablishState(FriendState.DELETE);

        return friendRepository.existsByTwoMemberAAndEstablishState(sourceMember.getId(), targetMember.getId(), FriendState.DELETE).isPresent();
    }


    /**
     * 친구 요청 취소
     * @param sourceMemberId -> 친구요청 취소를 요청한 UserId
     * @param targetMemberId -> 친구요청 대상 UserId
     */
    @Transactional
    public Boolean cancelFriends(Long sourceMemberId, Long targetMemberId) {
        Optional<Friend> friends = friendRepository.findBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.REQUEST);
        /* 해당 친구요청이 존재하지 않은 경우*/
        if (friends.isEmpty()) {
            throw new ApiException(ExceptionEnum.FRIENDS_REQUEST_NOT_FOUND);
        }
        friends.get().setEstablishState(FriendState.CANCEL);

        return friendRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.CANCEL);
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
        } else if (friendRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.REQUEST)){
            throw new ApiException(ExceptionEnum.FRIENDS_ALREADY_SENT);
        }
    }

    private boolean isFriendsRequestExist(Long sourceMemberId, Long targetMemberId, FriendState friendState) {
        return findByTwoMemberIdAndEstablishState(sourceMemberId, targetMemberId, friendState).isPresent();
    }

    private Optional<Friend> findByTwoMemberId(Long sourceMemberId, Long targetMemberId) {
        Optional <Friend> friends = friendRepository.findBySourceMemberIdAndTargetMemberId(sourceMemberId, targetMemberId);
        if (friends.isPresent()) {
            return friends;
        } else {
            return friendRepository.findBySourceMemberIdAndTargetMemberId(targetMemberId, sourceMemberId);
        }
    }

    public Optional<Friend> findByTwoMemberIdAndEstablishState(Long sourceMemberId, Long targetMemberId, FriendState friendState) {
        Optional<Friend> friends = friendRepository.findBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, friendState);
        if (friends.isPresent()) {
            return friends;
        } else {
            return friendRepository.findBySourceMemberIdAndTargetMemberIdAndEstablishState(targetMemberId, sourceMemberId, friendState);
        }
    }

    private List<Friend> findByMemberIdAndEstablishState(Long memberId, FriendState friendState) {
        List<Friend> friendList1 = friendRepository.findBySourceMemberIdAndEstablishState(memberId, friendState);
        List<Friend> friendList2 = friendRepository.findByTargetMemberIdAndEstablishState(memberId, friendState);
        return Stream.concat(friendList1.stream(), friendList2.stream()).collect(Collectors.toList());
    }

    public List<Member> findFriendsByMemberId(Long memberId, FriendState friendState) {
        if (!memberService.existById(memberId)){
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        }
        List<Friend> friendList = findByMemberIdAndEstablishState(memberId, friendState);
        return friendList.stream()
                .filter(friend -> memberRepository.findById(friend.getTargetMemberId()).get().getDisableDay() == null
                        && memberRepository.findById(friend.getSourceMemberId()).get().getDisableDay() == null)
                .map(friend -> findMemberInFriend(friend, memberId))
                .collect(Collectors.toList());
    }

    private Member findMemberInFriend(Friend friend, Long memberId) {
        if (friend.getSourceMemberId().equals(memberId)) {
            return memberService.findById(friend.getTargetMemberId());
        } else {
            return memberService.findById(friend.getSourceMemberId());
        }
    }

    public List<Member> findBySourceMemberIdAndFriendState(Long memberId, FriendState friendState) {
        if (!memberService.existById(memberId)){
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        }
        List<Friend> friendList = friendRepository.findBySourceMemberIdAndEstablishState(memberId, friendState);
        return friendList.stream()
                .filter(friend -> memberRepository.findById(friend.getTargetMemberId()).get().getDisableDay() == null)
                .map(friend -> memberService.findById(friend.getTargetMemberId()))
                .collect(Collectors.toList());
    }
    public List<Member> findByTargetMemberIdAndFriendState(Long memberId, FriendState friendState) {
        if (!memberService.existById(memberId)){
            throw new ApiException(ExceptionEnum.MEMBER_NOT_FOUND);
        }
        List<Friend> friendList = friendRepository.findByTargetMemberIdAndEstablishState(memberId, friendState);
        return friendList.stream()
                .filter(friend -> memberRepository.findById(friend.getSourceMemberId()).get().getDisableDay() == null)
                .map(friend -> memberService.findById(friend.getSourceMemberId())).
                collect(Collectors.toList());
    }
}
