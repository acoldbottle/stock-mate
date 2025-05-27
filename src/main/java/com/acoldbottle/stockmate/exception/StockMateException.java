package com.acoldbottle.stockmate.exception;

import lombok.Getter;

@Getter
public class StockMateException extends RuntimeException {

    private final ErrorCode errorCode;

    public StockMateException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public StockMateException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
