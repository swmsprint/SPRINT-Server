package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.Running;

@Repository
public interface RunningRepository extends JpaRepository<Running,Long> {

}
