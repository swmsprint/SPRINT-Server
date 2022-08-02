package sprint.server.controller.datatransferobject;

import lombok.Data;

/**
 * 러닝 생성후 반환할 응답
 */
@Data
public class CreateRunningResponse {
    private Long runningId;

    public CreateRunningResponse(Long runningId) {
        this.runningId = runningId;
    }
}
