package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.friends.FriendState;
import sprint.server.domain.friends.Friends;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long>{
    Boolean existsBySourceMemberIdAndTargetMemberIdAndEstablishState(Long sourceMemberId, Long targetMemberId, FriendState friendState);
    Optional<Friends> findBySourceMemberIdAndTargetMemberId(Long sourceMemberId, Long targetMemberId);
    Optional<Friends> findBySourceMemberIdAndTargetMemberIdAndEstablishState(Long sourceMemberId, Long targetMemberId, FriendState friendState);

    boolean existsBySourceMemberIdAndTargetMemberId(Long sourceMemberId, Long targetMemberId);

    List<Friends> findBySourceMemberIdAndEstablishState(Long memberId, FriendState friendState);
}
