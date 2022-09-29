package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sprint.server.domain.friend.FriendId;
import sprint.server.domain.friend.FriendState;
import sprint.server.domain.friend.Friend;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, FriendId>{
    Boolean existsBySourceMemberIdAndTargetMemberIdAndEstablishState(Long sourceMemberId, Long targetMemberId, FriendState friendState);

    @Query("select f from Friend f where (f.sourceMemberId = :sourceMemberId and f.targetMemberId = :targetMemberId and f.establishState = :friendState) or" +
            "(f.sourceMemberId = :targetMemberId and f.targetMemberId = :sourceMemberId and f.establishState = :friendState)")
    Optional<Friend> existsByTwoMemberAAndEstablishState(@Param("sourceMemberId") Long sourceMemberId, @Param("targetMemberId")Long targetMemberId, @Param("friendState")FriendState friendState);
    Optional<Friend> findBySourceMemberIdAndTargetMemberId(Long sourceMemberId, Long targetMemberId);
    Optional<Friend> findBySourceMemberIdAndTargetMemberIdAndEstablishState(Long sourceMemberId, Long targetMemberId, FriendState friendState);
    List<Friend> findBySourceMemberIdAndEstablishState(Long sourceMemberId, FriendState friendState);
    List<Friend> findByTargetMemberIdAndEstablishState(Long targetMemberId, FriendState friendState);
    void deleteBySourceMemberIdAndTargetMemberId(Long sourceMemberId, Long targetMemberId);
}
