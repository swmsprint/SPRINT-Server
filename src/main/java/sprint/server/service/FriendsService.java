package sprint.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.Member;
import sprint.server.domain.friends.Friends;
import sprint.server.repository.FriendsRepository;

@Service
@RequiredArgsConstructor
public class FriendsService {
    private final FriendsRepository friendsRepository;

    @Transactional //기본적으로 트렌젝션 안에서 되어야함
    public Friends addFriends(Member sourceMember, Member targetMember){
        Friends friends = Friends.createFriendsRelationship(sourceMember, targetMember);
        friendsRepository.save(friends);
        return friends;
    }

}
