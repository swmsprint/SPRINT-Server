package sprint.server.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.statistics.Statistics;
import sprint.server.domain.statistics.StatisticsType;

import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics,Long> {
    List<Statistics> findByStatisticsTypeAndMember_Id(StatisticsType statisticsType,long member_id);
}
