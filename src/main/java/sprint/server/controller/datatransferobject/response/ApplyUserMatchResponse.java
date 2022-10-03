package sprint.server.controller.datatransferobject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import sprint.server.domain.usermatch.UserMatchApplyId;

@Data
@AllArgsConstructor
public class ApplyUserMatchResponse {
    private UserMatchApplyId matchId;
}
