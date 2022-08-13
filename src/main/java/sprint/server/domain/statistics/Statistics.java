package sprint.server.domain.statistics;

import lombok.Getter;
import lombok.Setter;
import sprint.server.domain.member.Member;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Statistics {

    @Id @GeneratedValue
    @Column(name = "statistics_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private StatisticsType statisticsType;

    private double distance;
    private int totalSeconds;
    private int count;
    private double energy;

    private Timestamp time;

}
