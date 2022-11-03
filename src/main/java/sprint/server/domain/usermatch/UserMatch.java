package sprint.server.domain.usermatch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@Getter @Setter
public class UserMatch {

    @Id
    @GeneratedValue
    private Long matchId;

    private Long memberId;
    private Integer teamNumber;

    private Timestamp matchTime;

    private Integer ranking;
    private Integer totalScore;
    private Double totalDistance;
    private Integer totalCount;



}
