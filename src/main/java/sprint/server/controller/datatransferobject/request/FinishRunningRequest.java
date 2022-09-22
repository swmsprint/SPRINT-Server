package sprint.server.controller.datatransferobject.request;


import lombok.Data;
import lombok.NoArgsConstructor;
import sprint.server.domain.RunningRawData;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 러닝 종료후 전달받은 요청 정보
 */
@Data
@NoArgsConstructor
public class FinishRunningRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long runningId;
    @NotNull
    private Integer duration;

    @NotNull
    private Double distance;

    @NotNull
    private List<RunningRawData> runningData;

    public FinishRunningRequest(Long userId, Long runningId, Integer duration, Double distance, List<RunningRawData> runningData) {
        this.userId = userId;
        this.runningId = runningId;
        this.duration = duration;
        this.distance =distance;
        this.runningData = runningData;
    }
}
