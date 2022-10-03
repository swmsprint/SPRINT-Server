package sprint.server.domain.usermatch;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Embeddable
public class UserMatchApplyId implements Serializable {
    private Long memberId;
    private Timestamp applyTime;


    public UserMatchApplyId(Long memberId, Timestamp applyTime) {
        this.memberId = memberId;
        this.applyTime = applyTime;
    }
    public UserMatchApplyId() {}
}
