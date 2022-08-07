package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.friends.FriendState;
import sprint.server.domain.friends.Friends;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long>{
    Boolean existsBySourceMemberIdAndTargetMemberIdAndEstablishState(Long sourceMemberId, Long targetMemberId, FriendState friendState);
}
