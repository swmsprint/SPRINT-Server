package sprint.server.controller.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ExceptionDto {
    private final String errorCode;
    private final String errorMessage;

    @Builder
    public ExceptionDto(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
