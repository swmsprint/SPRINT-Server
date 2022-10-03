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

    private Long score;

    public UserMatchApply(UserMatchApplyId matchId, MatchStatus matchStatus, Long score) {
        this.matchId = matchId;
        this.matchStatus = matchStatus;
        this.score = score;
    }

    public UserMatchApply() {

    }
}
