package sprint.server.controller.datatransferobject.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LeagueInfoResponse {

    // 리그 정보
    // 리스트인데
    // 멤버 순위, 멤버 이름, 멤버 정보, 달린 총거리, 달린 횟수, 점수

    List<LeagueInfo> leagueInfoList;

}
