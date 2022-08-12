package sprint.server.controller.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ExceptionEnum {
    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST, "E0001"),
    ACCESS_DENIED_EXCEPTION(HttpStatus.UNAUTHORIZED, "E0002"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E0003"),
    SECURITY_01(HttpStatus.UNAUTHORIZED, "S0001", "권한이 없습니다."),
    FRIENDS_REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "F0001", "해당 친구요청이 존재하지 않습니다."),
    FRIENDS_ALREADY_SENT(HttpStatus.BAD_REQUEST, "F0002", "이미 전송된 요청입니다."),
    FRIENDS_ALREADY_FRIEND(HttpStatus.BAD_REQUEST, "F0003", "이미 친구입니다."),
    FRIENDS_NOT_FRIEND(HttpStatus.BAD_REQUEST, "F0004", "친구가 아닙니다."),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "M0001", "해당 유저가 존재하지 않습니다."),
    MEMBER_DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "M0002", "이미 존재하는 닉네임입니다."),
    MEMBER_DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "M0003", "이미 가입된 이메일입니다." );

    private final HttpStatus status;
    private final String code;
    private String messages;

    ExceptionEnum(HttpStatus status, String code) {
        this.status = status;
        this.code = code;
    }

    ExceptionEnum(HttpStatus status, String code, String messages) {
        this.status = status;
        this.code = code;
        this.messages = messages;
    }
}
