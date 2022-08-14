package sprint.server.controller.datatransferobject.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewRunningResponse {

    private Long runningId;
    private double distance;
    private double duration;
    private double energy;
    private List<RunningRawDataVO> runningData;
}
