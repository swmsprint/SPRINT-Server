package sprint.server.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@RequiredArgsConstructor
public class Running {
    @Id @GeneratedValue
    @Column(name = "running_id")
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "start_time")
    private Timestamp startTime;

    private int duration;

    private double distance;
    private double energy;

    private float weight;

    private String rowData;

    public static Running createRunning(Member member){
        Running running = new Running();
        running.setMember(member);
        return running;
    }

}