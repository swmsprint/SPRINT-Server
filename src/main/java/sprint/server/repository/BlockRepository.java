package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sprint.server.domain.block.Block;
import sprint.server.domain.block.BlockId;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, BlockId> {
    @Query("select b from Block b where (b.sourceMemberId =:sourceMemberId and b.targetMemberId =:targetMemberId) or " +
            "(b.sourceMemberId =:targetMemberId and b.targetMemberId =:sourceMemberId)")
    Optional<Block> findBlockByTwoMemberId(@Param("targetMemberId") Long targetMemberId,@Param("sourceMemberId") Long sourceMemberId);
    Optional<Block> findBlockBySourceMemberIdAndAndTargetMemberId(Long sourceMemberId, Long targetMemberId);
}
