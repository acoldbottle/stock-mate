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

    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주식을 찾을 수 없습니다."),
    STOCK_SEARCH_COND(HttpStatus.BAD_REQUEST, "검색어는 공백일 수 없습니다."),

    HOLDING_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 보유 종목을 찾을 수 없습니다."),

    WATCH_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 관심종목을 찾을 수 없습니다."),
    WATCH_ITEM_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 관심종목에 등록되어 있습니다."),

    KIS_FILE_DOWNLOAD_ERROR(HttpStatus.NOT_FOUND, "한국투자증권 주식종목파일 다운로드에 실패했습니다."),
    KIS_FILE_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "한국투자증권 주식종목파일 파싱에 실패했습니다"),
    KIS_TOKEN_REISSUE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "한국투자증권 토큰 발급에 실패했습니다"),
    KIS_CURRENT_PRICE_ERROR(HttpStatus.BAD_GATEWAY, "현재 시세를 가져오지 못 했습니다."),
    KIS_TOO_MANY_REQUEST(HttpStatus.TOO_MANY_REQUESTS, "요청 한도를 초과하여 현재 시세를 가져오지 못했습니다."),
    KIS_REQUEST_INTERRUPTED_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "한국투자증권 현재 시세 요청 중에 인터럽트가 발생하였습니다."),

    INVALID_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "필드가 잘 못 되었습니다."),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "잘못된 요청 형식입니다. JSON 형식을 확인하세요."),
    INVALID_REQUEST_PARAM(HttpStatus.BAD_REQUEST, "잘못된 요청 형식입니다. 파라미터를 확인하세요."),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "접근 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    REDIS_SAVE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "레디스에 저장하지 못 했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
