package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sprint.server.domain.friends.FriendState;
import sprint.server.domain.friends.Friends;
import sprint.server.domain.friends.FriendsId;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, FriendsId>{
    Boolean existsBySourceMemberIdAndTargetMemberIdAndEstablishState(Long sourceMemberId, Long targetMemberId, FriendState friendState);
    Optional<Friends> findBySourceMemberIdAndTargetMemberId(Long sourceMemberId, Long targetMemberId);
    Optional<Friends> findBySourceMemberIdAndTargetMemberIdAndEstablishState(Long sourceMemberId, Long targetMemberId, FriendState friendState);
    List<Friends> findBySourceMemberIdAndEstablishState(Long sourceMemberId, FriendState friendState);
    List<Friends> findByTargetMemberIdAndEstablishState(Long targetMemberId, FriendState friendState);
}
