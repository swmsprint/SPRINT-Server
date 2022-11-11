package sprint.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {
    private final FriendRepository friendRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    /**
     * 친구 요청
     * @param sourceMember : 친구 요청을 보내는 유저
     * @param targetMember : 친구 요청을 받을 유저
     * @return
     */
    @Transactional
    public Boolean requestFriends(Member sourceMember, Member targetMember) {
        log.info("{} -> {}, 친구 등록 요청", sourceMember.getId(), targetMember.getId());
        if (sourceMember.equals(targetMember)) {
            log.error("{} -> {}, 동일 유저에게 친구 신청 불가", sourceMember.getId(), targetMember.getId());
            throw new ApiException(ExceptionEnum.FRIEND_INVALID_REQUEST);
        }
        Optional<Friend> friend = friendRepository.findFriendByTwoMemberId(sourceMember.getId(), targetMember.getId());
        Long sourceMemberId = sourceMember.getId();
        Long targetMemberId = targetMember.getId();


        if (friend.isPresent()) {
            Friend existFriend = friend.get();

            /* 이미 친구관계인지 검사 */
            if (existFriend.getEstablishState().equals(FriendState.ACCEPT)) {
                log.error("{} -> {}, 이미 친구 관계 존재", sourceMember.getId(), targetMember.getId());
                throw new ApiException(ExceptionEnum.FRIEND_ALREADY_FRIEND);
            }

            if (existFriend.getEstablishState().equals(FriendState.REQUEST)) {
                /* 만약 이미 targetMemberId가 sourceMemberId에게 친구요청을 보낸적이 있다면 */
                if (existFriend.getSourceMemberId().equals(targetMemberId)){
                    log.info("{} -> {}, 친구 추가 요청 존재, 친구 등록", targetMemberId, sourceMember);
                    acceptFriendsRequest(targetMember, sourceMember);
                    return true;
                }
                /* 해당 요청이 이미 전송되었는지 여부를 검사 */
                else {
                    log.error("{} -> {}, 이미 친구 요청 존재", sourceMember.getId(), targetMember.getId());
                    throw new ApiException(ExceptionEnum.FRIEND_ALREADY_SENT);
                }
            }

            /* 만약 이미 targetMember가 요청한 신청을 DELETE, REJECT, CANCEL한 관계라면 기존 관계 삭제*/
            if(existFriend.getSourceMemberId().equals(targetMember.getId())){
                friendRepository.deleteBySourceMemberIdAndTargetMemberId(targetMemberId, sourceMemberId);
            }
        }

        /* 새로운 관계 생성 */
        Friend newFriend = new Friend(sourceMember, targetMember);
        friendRepository.save(newFriend);
        log.info("{} -> {}, 친구 등록 완료", sourceMember.getId(), targetMember.getId());
        return friendRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.REQUEST);
    }

    /**
     * 친구 요청 거절
     * @param sourceMember : 친구 요청을 보낸 사용자
     * @param targetMember : 친구 요청을 거절하는 사용자
     * @return
     */
    @Transactional
    public Boolean rejectFriendsRequest(Member sourceMember , Member targetMember){
        log.info("{} -> {}, 친구 요청 거절 요청", sourceMember.getId(), targetMember.getId());
        if (sourceMember.equals(targetMember)) {
            log.error("{} -> {}, 동일 유저 에러", sourceMember.getId(), targetMember.getId());
            throw new ApiException(ExceptionEnum.FRIEND_INVALID_REQUEST);
        }
        Optional<Friend> friend = friendRepository.findBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMember.getId(), targetMember.getId(), FriendState.REQUEST);
        if (friend.isEmpty()){
            log.error("{} -> {}, 해당 친구추가 요청 검색 실패", sourceMember.getId(), targetMember.getId());
            throw new ApiException(ExceptionEnum.FRIEND_REQUEST_NOT_FOUND);
        }
        friend.get().setReject();
        log.info("{} -> {}, 친구 요청 거절 성공", sourceMember.getId(), targetMember.getId());
        return friend.get().getEstablishState().equals(FriendState.REJECT);
    }

    /**
     * 친구 요청 수락
     * @param sourceMember : 친구 요청을 보낸 사용자
     * @param targetMember : 친구 요청을 수락할 사용자
     * @return
     */
    @Transactional
    public Boolean acceptFriendsRequest(Member sourceMember, Member targetMember){
        log.info("{} -> {}, 친구 요청 수락 요청",sourceMember.getId(), targetMember.getId());
        if (sourceMember.equals(targetMember)) {
            log.error("{} -> {}, 동일 유저 에러",sourceMember.getId(), targetMember.getId());
            throw new ApiException(ExceptionEnum.FRIEND_INVALID_REQUEST);
        }
        Optional<Friend> existFriend = friendRepository.findBySourceMemberIdAndTargetMemberId(sourceMember.getId(), targetMember.getId());

        /* 이미 친구인 경우 */
        if (existFriend.isPresent() && existFriend.get().getEstablishState().equals(FriendState.ACCEPT)) {
            log.error("{} -> {}, 이미 친구관계 존재",sourceMember.getId(), targetMember.getId());
            throw new ApiException(ExceptionEnum.FRIEND_ALREADY_FRIEND);
        }

        /* 해당 친구요청이 존재하지 않은 경우 */
        if (existFriend.isEmpty() || !existFriend.get().getEstablishState().equals(FriendState.REQUEST)) {
            log.error("{} -> {}, 친구 관계 요청 검색 실패",sourceMember.getId(), targetMember.getId());
            throw new ApiException(ExceptionEnum.FRIEND_REQUEST_NOT_FOUND);
        }

        existFriend.get().setAccept();
        log.info("{} -> {}, 친구 요청 응답 수락",sourceMember.getId(), targetMember.getId());
        return existFriend.get().getEstablishState().equals(FriendState.ACCEPT);
    }

    /**
     * 친구 제거
     * @param sourceMember : 친구 제거를 요청한 사용자
     * @param targetMember : sourceMember와 친구관계인 사용자
     * @return 결과(true/false)
     */
    @Transactional
    public Boolean deleteFriends(Member sourceMember, Member targetMember) {
        log.info("{} -> {}, 친구 제거 요청",sourceMember.getId(), targetMember.getId());
        if (sourceMember.equals(targetMember)) {
            log.error("{} -> {}, 동일 유저 에러",sourceMember.getId(), targetMember.getId());
            throw new ApiException(ExceptionEnum.FRIEND_INVALID_REQUEST);
        }
        Optional<Friend> friend = friendRepository.findByTwoMemberAndEstablishState(sourceMember.getId(), targetMember.getId(), FriendState.ACCEPT);

        /* 해당 친구관계가 존재하지 않은 경우 */
        if(friend.isEmpty()) {
            log.error("{} -> {}, 친구 관계 요청 검색 실패",sourceMember.getId(), targetMember.getId());
            throw new ApiException(ExceptionEnum.FRIEND_NOT_FOUND);
        }

        friend.get().setDelete();
        log.info("{} -> {}, 친구 제거 완료",sourceMember.getId(), targetMember.getId());
        return friend.get().getEstablishState().equals(FriendState.DELETE);
    }

    /**
     * 친구 요청 취소
     * @param sourceMember : 친구 요청 취소를 요청한 사용자
     * @param targetMember : 친구 요청 대상 사용자
     * @return
     */
    @Transactional
    public Boolean cancelFriends(Member sourceMember, Member targetMember) {
        log.info("{} -> {}, 친구 요청 취소 요청", sourceMember.getId(), targetMember.getId());
        if (sourceMember.equals(targetMember)) {
            log.error("{} -> {}, 동일 유저 에러",sourceMember.getId(), targetMember.getId());
            throw new ApiException(ExceptionEnum.FRIEND_INVALID_REQUEST);
        }
        Optional<Friend> friend = friendRepository.findBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMember.getId(), targetMember.getId(), FriendState.REQUEST);

        /* 해당 친구요청이 존재하지 않은 경우*/
        if (friend.isEmpty()) {
            log.error("{} -> {}, 친구 관계 요청 검색 실패",sourceMember.getId(), targetMember.getId());
            throw new ApiException(ExceptionEnum.FRIEND_REQUEST_NOT_FOUND);
        }

        friend.get().setCancel();
        log.info("{} -> {}, 친구 요청 취소 성공", sourceMember.getId(), targetMember.getId());
        return friend.get().getEstablishState().equals(FriendState.CANCEL);
    }

    public Optional<Friend> findByTwoMemberIdAndEstablishState(Long sourceMemberId, Long targetMemberId, FriendState friendState) {
        Optional<Friend> friends = friendRepository.findBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, friendState);
        if (friends.isPresent()) {
            return friends;
        } else {
            return friendRepository.findBySourceMemberIdAndTargetMemberIdAndEstablishState(targetMemberId, sourceMemberId, friendState);
        }
    }

    public List<Member> findFriendsByMemberId(Member member, FriendState friendState) {
        log.info("ID : {}, 친구 관계({}) 요청",member.getId(), friendState);
        List<Friend> friendList = friendRepository.findFriendsByMemberIdAndEstablishState(member.getId(), friendState);
        return friendList.stream()
                .filter(friend -> memberRepository.findById(friend.getTargetMemberId()).get().getDisableDay() == null
                        && memberRepository.findById(friend.getSourceMemberId()).get().getDisableDay() == null)
                .map(friend -> findMemberInFriend(friend, member.getId()))
                .collect(Collectors.toList());
    }

    private Member findMemberInFriend(Friend friend, Long memberId) {
        if (friend.getSourceMemberId().equals(memberId)) {
            return memberService.findById(friend.getTargetMemberId());
        } else {
            return memberService.findById(friend.getSourceMemberId());
        }
    }

    public List<Member> findBySourceMemberIdAndFriendState(Member member, FriendState friendState) {
        List<Friend> friendList = friendRepository.findBySourceMemberIdAndEstablishState(member.getId(), friendState);
        return friendList.stream()
                .filter(friend -> memberRepository.findById(friend.getTargetMemberId()).get().getDisableDay() == null)
                .map(friend -> memberService.findById(friend.getTargetMemberId()))
                .collect(Collectors.toList());
    }

    public List<Member> findByTargetMemberIdAndFriendState(Member member, FriendState friendState) {
        List<Friend> friendList = friendRepository.findByTargetMemberIdAndEstablishState(member.getId(), friendState);
        return friendList.stream()
                .filter(friend -> memberRepository.findById(friend.getSourceMemberId()).get().getDisableDay() == null)
                .map(friend -> memberService.findById(friend.getSourceMemberId())).
                collect(Collectors.toList());
    }
}
