package sprint.server.controller.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException{
    private ExceptionEnum error;
    private String errorCode;
    public ApiException(ExceptionEnum e) {
        super(e.getMessages());
        this.error = e;
        this.errorCode = e.getCode();
    }
}
