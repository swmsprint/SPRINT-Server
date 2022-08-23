package sprint.server.controller.datatransferobject.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StatisticsInfoVO {
    private double distance;
    private double totalSeconds;
    private double energy;

    @Builder
    public StatisticsInfoVO(double distance, double totalSeconds, double energy) {
        this.distance = distance;
        this.totalSeconds = totalSeconds;
        this.energy = energy;
    }
}
