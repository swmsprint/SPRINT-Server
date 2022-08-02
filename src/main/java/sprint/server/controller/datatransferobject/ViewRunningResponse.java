package sprint.server.controller.datatransferobject;

import lombok.Data;
import sprint.server.domain.RunningRowData;

import java.util.List;

@Data
public class ViewRunningResponse {
    private long runningId;
    private double distance;
    private double duration;
    private double energy;
    private List<RunningRowData> runningData;

    public ViewRunningResponse(long runningId, double distance, double duration, double energy, List<RunningRowData> runningData) {
        this.runningId = runningId;
        this.distance = distance;
        this.duration = duration;
        this.energy = energy;
        this.runningData = runningData;
    }
}
