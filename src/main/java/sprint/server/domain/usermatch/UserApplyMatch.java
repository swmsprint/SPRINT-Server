package sprint.server.domain.usermatch;

import lombok.Getter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Getter
public class UserApplyMatch {

    @EmbeddedId
    private UserApplyMatchId matchId;

    private MatchStatus matchStatus;
}
