package sprint.server.domain.usermatch;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Embeddable
public class UserApplyMatchId implements Serializable {
    private Long memberId;
    private Timestamp applyTime;

}
