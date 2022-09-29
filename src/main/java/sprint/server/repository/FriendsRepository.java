package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sprint.server.domain.friends.FriendState;
import sprint.server.domain.friends.Friends;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long>{
    Boolean existsBySourceMemberIdAndTargetMemberIdAndEstablishState(Long sourceMemberId, Long targetMemberId, FriendState friendState);

    @Query("select f from Friends f where (f.sourceMemberId = :sourceMemberId and f.targetMemberId = :targetMemberId and f.establishState = :friendState) or" +
            "(f.sourceMemberId = :targetMemberId and f.targetMemberId = :sourceMemberId and f.establishState = :friendState)")
    Optional<Friends> existsByTwoMemberAAndEstablishState(@Param("sourceMemberId") Long sourceMemberId, @Param("targetMemberId")Long targetMemberId, @Param("friendState")FriendState friendState);
    Optional<Friends> findBySourceMemberIdAndTargetMemberId(Long sourceMemberId, Long targetMemberId);
    Optional<Friends> findBySourceMemberIdAndTargetMemberIdAndEstablishState(Long sourceMemberId, Long targetMemberId, FriendState friendState);
    List<Friends> findBySourceMemberIdAndEstablishState(Long sourceMemberId, FriendState friendState);
    List<Friends> findByTargetMemberIdAndEstablishState(Long targetMemberId, FriendState friendState);
}
