package sprint.server.controller.datatransferobject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import sprint.server.domain.RunningRowData;

import java.util.List;

@Data
@AllArgsConstructor
public class ViewRunningResponse {
    private long runningId;
    private double distance;
    private double duration;
    private double energy;
    private List<RunningRowData> runningData;
}
