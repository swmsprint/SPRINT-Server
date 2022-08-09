package sprint.server.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.statistics.Statistics;
import sprint.server.domain.statistics.StatisticsType;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics,Long> {

    List<Statistics> findByStatisticsTypeAndMember_Id(StatisticsType statisticsType,long member_id);
    Statistics findByStatisticsTypeAndMember_IdAndTimeBetween( StatisticsType statisticsType,long id, Timestamp startTime,Timestamp endTime);
    Statistics findByTimeBetween(Timestamp start, Timestamp finish);
}

