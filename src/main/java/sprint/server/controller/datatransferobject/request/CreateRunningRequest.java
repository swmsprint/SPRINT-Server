package sprint.server.controller.datatransferobject.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;

/**
 * 러닝 생성후 반환할 응답
 */
@Data
@NoArgsConstructor
public class CreateRunningRequest {

    @NotNull
    @JsonProperty("userId")
    private Long userId;

    @NotNull @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss.SSS")
    @JsonProperty("startTime")
    private String startTime;

    public CreateRunningRequest(Long userId, String startTime) {
        this.userId = userId;
        this.startTime = startTime;
    }

}
