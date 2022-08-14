package sprint.server.controller.datatransferobject.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import sprint.server.controller.datatransferobject.response.RunningRawDataVO;

import java.util.List;

/**
 * 러닝 종료후 전달받은 요청 정보
 */
@Data
@NoArgsConstructor
public class FinishRunningRequest {
    private Long userId;
    private Long runningId;
    private double duration;
    private double distance;
    private List<RunningRawDataVO> runningData;

    public FinishRunningRequest(Long userId, Long runningId, int duration, List<RunningRawDataVO> runningData) {
        this.userId = userId;
        this.runningId = runningId;
        this.duration = duration;
        this.runningData = runningData;
    }
}
