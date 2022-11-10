package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sprint.server.domain.block.GlobalBlock;

public interface GlobalBlockRepository extends JpaRepository<GlobalBlock, Long> {
}
