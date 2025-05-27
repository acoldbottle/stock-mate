package com.acoldbottle.stockmate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    private HttpStatus httpStatus;
    private String message;

    public ErrorResponse(StockMateException e) {
        ErrorCode errorCode = e.getErrorCode();
        this.httpStatus = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
