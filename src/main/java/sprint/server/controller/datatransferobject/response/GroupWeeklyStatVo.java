package sprint.server.controller.datatransferobject.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupWeeklyStatVo {
    private double totalTime;
    private double totalDistance;
}
