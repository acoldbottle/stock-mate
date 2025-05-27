package com.acoldbottle.stockmate.exception;

public class UserAlreadyExistsException extends StockMateException{
    public UserAlreadyExistsException() {
        super(ErrorCode.USER_ALREADY_EXISTS);
    }

    public UserAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
