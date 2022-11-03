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
    boolean existsBySourceMemberIdAndTargetMemberIdAndEstablishState(Long sourceMemberId, Long targetMemberId, FriendState friendState);

    @Query("select f from Friend f where (f.sourceMemberId = :sourceMemberId and f.targetMemberId = :targetMemberId and f.establishState = :friendState) or" +
            "(f.sourceMemberId = :targetMemberId and f.targetMemberId = :sourceMemberId and f.establishState = :friendState)")
    Optional<Friend> findByTwoMemberAndEstablishState(@Param("sourceMemberId") Long sourceMemberId, @Param("targetMemberId")Long targetMemberId, @Param("friendState")FriendState friendState);

    @Query("select f from Friend f where (f.sourceMemberId =:sourceMemberId and f.targetMemberId =:targetMemberId) or " +
            "(f.sourceMemberId =:targetMemberId and f.targetMemberId =:sourceMemberId)")
    Optional<Friend> findFriendByTwoMemberId(@Param("sourceMemberId") Long sourceMemberId, @Param("targetMemberId") Long targetMemberId);

    @Query("select f from Friend f where (f.sourceMemberId =:memberId or f.targetMemberId =:memberId) and f.establishState =:state")
    List<Friend> findFriendsByMemberIdAndEstablishState(@Param("memberId") Long memberId, @Param("state") FriendState state);
    Optional<Friend> findBySourceMemberIdAndTargetMemberId(Long sourceMemberId, Long targetMemberId);
    Optional<Friend> findBySourceMemberIdAndTargetMemberIdAndEstablishState(Long sourceMemberId, Long targetMemberId, FriendState friendState);
    List<Friend> findBySourceMemberIdAndEstablishState(Long sourceMemberId, FriendState friendState);
    List<Friend> findByTargetMemberIdAndEstablishState(Long targetMemberId, FriendState friendState);
    void deleteBySourceMemberIdAndTargetMemberId(Long sourceMemberId, Long targetMemberId);
}
