package sprint.server.controller.datatransferobject;

import lombok.Builder;
import lombok.Getter;

import java.awt.geom.Point2D;

@Getter
public class StatisticsDTO {
    private double distance;
    private double totalSeconds;
    private double energy;
    private double pace;

    @Builder
    public StatisticsDTO(double distance, double totalSeconds, double energy, double pace) {
        this.distance = distance;
        this.totalSeconds = totalSeconds;
        this.energy = energy;
        this.pace = pace;
    }
}
