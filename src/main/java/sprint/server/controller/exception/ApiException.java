package sprint.server.controller.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException{
    private final ExceptionEnum error;
    private final String errorCode;
    public ApiException(ExceptionEnum e) {
        super(e.getMessages());
        this.error = e;
        this.errorCode = e.getCode();
    }
    public ApiException(ExceptionEnum e, String messages){
        super(messages);
        this.error = e;
        this.errorCode = e.getCode();
    }
}
