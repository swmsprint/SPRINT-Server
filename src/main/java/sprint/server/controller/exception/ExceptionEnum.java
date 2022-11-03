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
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "F0001", "해당 친구요청이 존재하지 않습니다."),
    FRIEND_ALREADY_SENT(HttpStatus.BAD_REQUEST, "F0002", "이미 전송된 요청입니다."),
    FRIEND_ALREADY_FRIEND(HttpStatus.BAD_REQUEST, "F0003", "이미 친구입니다."),
    FRIEND_NOT_FOUND(HttpStatus.BAD_REQUEST, "F0004", "친구가 아닙니다."),
    FRIEND_METHOD_NOT_FOUND(HttpStatus.BAD_REQUEST, "F0005", "요청 메서드가 잘못 되었습니다."),
    FRIEND_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "G0006", "잘못된 요청입니다." ),
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "M0001", "해당 유저가 존재하지 않습니다."),
    MEMBER_DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "M0002", "이미 존재하는 닉네임입니다."),
    MEMBER_DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "M0003", "이미 가입된 이메일입니다." ),
    MEMBER_NOT_DISABLED(HttpStatus.BAD_REQUEST, "M0004", "이미 활성화된 계정입니다." ),
    MEMBER_ALREADY_DISABLED(HttpStatus.BAD_REQUEST, "M0005", "이미 비활성화된 계정입니다."),
    MEMBER_ALREADY_SIGNUP(HttpStatus.BAD_REQUEST, "M0006", "이미 가입된 계정입니다."  ),
    GROUP_NAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "G0001", "이미 존재하는 그룹 이름입니다."),
    GROUP_NOT_FOUND(HttpStatus.BAD_REQUEST, "G0002", "해당 그룹이 존재하지 않습니다." ),
    GROUP_REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "G0003", "해당 그룹 가입 요청이 존재하지 않습니다."),
    GROUP_ALREADY_JOINED(HttpStatus.BAD_REQUEST, "G0004",  "이미 가입된 그룹입니다."),
    GROUP_LEADER_CANT_LEAVE(HttpStatus.BAD_REQUEST, "G0005", "그룹 리더는 탈퇴할 수 없습니다."),
    GROUP_MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "G0006", "해당 유저는 그룹 멤버가 아닙니다."),
    GROUP_METHOD_NOT_FOUND(HttpStatus.BAD_REQUEST, "G0007", "요청 메서드가 잘못 되었습니다."),
    GROUP_NOT_LEADER(HttpStatus.BAD_REQUEST, "G0008", "그룹장이 아닙니다."),
    GROUP_DELETED(HttpStatus.BAD_REQUEST,"G0009", "삭제된 그룹입니다."),
    GROUP_ALREADY_REQUESTED(HttpStatus.BAD_REQUEST, "G0010", "이미 전송된 그룹 가입 요청입니다."),
    GROUP_ALREADY_LEADER(HttpStatus.BAD_REQUEST, "G0011", "이미 그룹장입니다."),
    GROUP_PERSONNEL_FULL(HttpStatus.BAD_REQUEST, "G0012", "그룹원 한도를 초과했습니다" ),
    TOKEN_NONE_PERMISSION(HttpStatus.UNAUTHORIZED, "T0001","권한 정보가 없는 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "T0002", "만료된 토큰입니다."),
    TOKEN_SIGNITURE_ERROR(HttpStatus.UNAUTHORIZED, "T0003", "JWT 서명의 형식이 잘못되었습니다."),
    TOKEN_NOT_SUPPORT(HttpStatus.UNAUTHORIZED, "T0004", "지원하지 않는 JWT 입니다."),
    TOKEN_ILLEGAL_JWT(HttpStatus.UNAUTHORIZED,"T0005", "잘못된 JWT 입니다."),
    TOKEN_NOT_OWNER(HttpStatus.UNAUTHORIZED, "T0006", "JWT와 요청한 유저가 일치하지 않습니다." ),
    TOKEN_REQUIRE_REFRESHTOKEN(HttpStatus.UNAUTHORIZED, "T0007", "Refresh Token이 아닙니다.");

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
