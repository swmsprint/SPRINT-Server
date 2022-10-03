package sprint.server.domain.usermatch;

import lombok.Getter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Enumerated;

@Entity
@Getter
public class UserMatchApply {

    @EmbeddedId
    private UserMatchApplyId matchId;

    @Enumerated
    private MatchStatus matchStatus;

    public UserMatchApply(UserMatchApplyId matchId, MatchStatus matchStatus) {
        this.matchId = matchId;
        this.matchStatus = matchStatus;
    }

    public UserMatchApply() {

    }
}
