package sprint.server.domain.usermatch;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@Getter
public class UserMatch {

    @Id
    @GeneratedValue
    private Long matchId;

    private Long memberId;
    private Long teamNumber;

    private Timestamp matchTime;

    private Integer ranking;
    private Integer totalScore;

}
