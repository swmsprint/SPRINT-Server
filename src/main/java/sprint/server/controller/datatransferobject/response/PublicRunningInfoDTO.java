package sprint.server.controller.datatransferobject.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PublicRunningInfoDTO {
    private Long runningId;
    private Long memberId;
    private double duration;
    private double distance;
    private String startTime;
    private double energy;

    @Builder
    public PublicRunningInfoDTO(Long runningId, Long memberId, double duration, double distance, String startTime, double energy) {
        this.runningId = runningId;
        this.memberId = memberId;
        this.duration = duration;
        this.distance = distance;
        this.startTime = startTime;
        this.energy = energy;
    }
}
