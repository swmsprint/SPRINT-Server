package sprint.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.friends.FriendState;
import sprint.server.domain.friends.Friends;
import sprint.server.repository.FriendsRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendsService {
    private final FriendsRepository friendsRepository;
    private final MemberService memberService;

    @Transactional
    public Friends FriendsRequest(Long sourceMemberId, Long targetMemberId) {
        validationFriendsRequest(sourceMemberId, targetMemberId);
        Friends friends = Friends.createFriendsRelationship(sourceMemberId, targetMemberId);
        friendsRepository.save(friends);
        return friends;
    }
    private void validationFriendsRequest(Long sourceMemberId, Long targetMemberId){
        memberService.isMemberExistById(sourceMemberId, "sourceMember가 database에 존재하지 않습니다.");
        memberService.isMemberExistById(targetMemberId, "targetMember가 database에 존재하지 않습니다.");
        boolean isExists = friendsRepository.existsBySourceMemberIdAndTargetMemberIdAndEstablishState(sourceMemberId, targetMemberId, FriendState.REQUEST);
        if(isExists){
            throw new IllegalStateException("이미 전송된 요청입니다.");
        }
    }
}
