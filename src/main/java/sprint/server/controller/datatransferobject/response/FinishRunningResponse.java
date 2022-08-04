package sprint.server.controller.datatransferobject.response;

import lombok.Data;

/**
 * 러닝 종료 후 반환할 응답
 */
@Data
public class FinishRunningResponse {
    private long runningId;
    private double distance;
    private double duration;
    private double energy;

    public FinishRunningResponse(long runningId, double distance, double duration, double energy) {
        this.runningId = runningId;
        this.distance = distance;
        this.duration = duration;
        this.energy = energy;
    }
}
