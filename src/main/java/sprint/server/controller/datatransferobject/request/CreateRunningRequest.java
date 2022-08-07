package sprint.server.controller.datatransferobject.request;

import lombok.Data;

import java.sql.Time;
import java.time.LocalDateTime;

/**
 * 러닝 생성후 반환할 응답
 */
@Data
public class CreateRunningRequest {
    private Long userId;
    private String startTime;
}
