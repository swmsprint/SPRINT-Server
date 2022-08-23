package sprint.server.controller.datatransferobject.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 러닝 생성후 반환할 응답
 */
@Data
@NoArgsConstructor
public class CreateRunningRequest {

    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("startTime")
    private String startTime;

    public CreateRunningRequest(Long userId, String startTime) {
        this.userId = userId;
        this.startTime = startTime;
    }

}
