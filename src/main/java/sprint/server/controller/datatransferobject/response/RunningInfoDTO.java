package sprint.server.controller.datatransferobject.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RunningInfoDTO {

    private Long runningId;
    private int duration;
    private double distance;
    private String startTime;
    private double energy;
    @Builder

    public RunningInfoDTO(Long runningId, int duration, double distance, String startTime, double energy) {
        this.runningId = runningId;
        this.duration = duration;
        this.distance = distance;
        this.startTime = startTime;
        this.energy = energy;
    }
}
