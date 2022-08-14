package sprint.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.Running;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface RunningRepository extends JpaRepository<Running,Long> {

    Page<Running> findByIdLessThanAndMemberIdOrderByIdDesc(Long lastRunningId, Long memberId, PageRequest pageRequest);

    Running findByMember_IdAndAndStartTime(Long memberId, Timestamp startTime);
}
