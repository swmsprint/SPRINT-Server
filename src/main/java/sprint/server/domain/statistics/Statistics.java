package sprint.server.domain.statistics;

import lombok.Getter;
import lombok.Setter;
import sprint.server.domain.Member;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Statistics {

    @Id @GeneratedValue
    @Column(name = "statistics_id")
    private long id;

    @Column(name = "user_id")
    private long member_id;

    @Enumerated(EnumType.STRING)
    private StatisticsType statistics_type;

    private double distance;
    private int duration;
    private int averageTime;
    private double energy;

    private LocalDateTime saveTime;

    public static Statistics createStatistics(Member member, StatisticsType statisticsType, double distance, int duration,  int averagetime, double energy){
        Statistics statistics = new Statistics();
        statistics.setMember_id(member.getId());
        statistics.setStatistics_type(statisticsType);
        statistics.setDistance(distance);
        statistics.setDuration(duration);
        statistics.setAverageTime(averagetime);
        statistics.setEnergy(energy);
        return statistics;
    }
}
