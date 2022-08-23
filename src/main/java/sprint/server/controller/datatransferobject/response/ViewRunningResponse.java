package sprint.server.controller.datatransferobject.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sprint.server.domain.RunningRawData;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewRunningResponse {

    private Long runningId;
    private double distance;
    private double duration;
    private double energy;
    private List<RunningRawData> runningData;
}
