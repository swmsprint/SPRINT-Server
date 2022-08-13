package sprint.server.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.statistics.Statistics;
import sprint.server.domain.statistics.StatisticsType;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics,Long> {

    List<Statistics> findAllByStatisticsTypeAndMemberId(StatisticsType statisticsType, long memberId);
    List<Statistics> findAllByStatisticsTypeAndMemberIdAndTimeBetween(StatisticsType statisticsType, long memberId, Timestamp startTime, Timestamp endTime);
    Statistics findByStatisticsTypeAndMemberIdAndTimeBetween(StatisticsType statisticsType, long id, Timestamp startTime, Timestamp endTime);
}

