package sprint.server.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionController {

    @ExceptionHandler({ApiException.class})
    public ResponseEntity<ExceptionDto> exceptionHandler(HttpServletRequest request, final ApiException e){
        return ResponseEntity
                .status(e.getError().getStatus())
                .body(ExceptionDto.builder()
                        .errorCode(e.getErrorCode())
                        .errorMessage(e.getMessage())
                        .build());
    }
}
