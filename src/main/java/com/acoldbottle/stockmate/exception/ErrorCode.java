package com.acoldbottle.stockmate.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 회원 아이디입니다."),
    USER_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    PORTFOLIO_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 포트폴리오를 찾을 수 없습니다."),

    INVALID_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "필드가 잘 못 되었습니다."),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "잘못된 요청 형식입니다. JSON 형식을 확인하세요."),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "접근 권한이 없습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),

    KIS_TOKEN_REISSUE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "한국투자증권 토큰 발급에 실패했습니다");

    private final HttpStatus httpStatus;
    private final String message;
}
