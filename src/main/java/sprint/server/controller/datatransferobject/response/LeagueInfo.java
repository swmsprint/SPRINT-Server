package sprint.server.controller.datatransferobject.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeagueInfo {

    Integer teamNumber;
    Long memberId;
    String memberName;
    Double totalDistance;
    Integer totalCount;
    Integer totalScore;
    Integer ranking;
    Long matchId;

}
