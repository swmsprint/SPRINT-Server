package sprint.server.controller.datatransferobject.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StatisticsInfoVO {
    private double distance;
    private double totalSeconds;
    private double energy;
    private int count;

    @Builder
    public StatisticsInfoVO(double distance, double totalSeconds, double energy, int count) {
        this.distance = distance;
        this.totalSeconds = totalSeconds;
        this.energy = energy;
        this.count = count;
    }
}
