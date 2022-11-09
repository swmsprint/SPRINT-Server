package sprint.server.domain.statistics;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sprint.server.domain.member.Member;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Statistics {

    @Id @GeneratedValue
    @Column(name = "statistics_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private StatisticsType statisticsType;

    private double distance;
    private double totalSeconds;
    private int count;
    private double energy;

    private Timestamp time;

    @Builder
    public Statistics(Member member, StatisticsType statisticsType, double distance, double totalSeconds, int count, double energy, Timestamp time) {
        this.member = member;
        this.statisticsType = statisticsType;
        this.distance = distance;
        this.totalSeconds = totalSeconds;
        this.count = count;
        this.energy = energy;
        this.time = time;
    }
}
