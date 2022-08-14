package sprint.server.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import sprint.server.domain.member.Member;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter @Setter
@RequiredArgsConstructor
public class Running {
    @Id @GeneratedValue
    @Column(name = "running_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "start_time")
    private Timestamp startTime;

    private double duration;
    private double distance;
    private double energy;
    private float weight;

    @Column(length = 10000)
    private String rawData;


}