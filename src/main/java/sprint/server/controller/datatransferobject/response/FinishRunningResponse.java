package sprint.server.controller.datatransferobject.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 러닝 종료 후 반환할 응답
 */
@Data
@AllArgsConstructor
public class FinishRunningResponse {
    private Long runningId;
    private double distance;
    private double duration;
    private double energy;
}
