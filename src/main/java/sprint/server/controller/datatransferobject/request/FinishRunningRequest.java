package sprint.server.controller.datatransferobject.request;

import lombok.Data;
import sprint.server.domain.RunningRowData;

import java.util.List;

/**
 * 러닝 종료후 전달받은 요청 정보
 */
@Data
public class FinishRunningRequest {
    private Long userId;
    private Long runningId;
    private int duration;
    private List<RunningRowData> runningData;
}
