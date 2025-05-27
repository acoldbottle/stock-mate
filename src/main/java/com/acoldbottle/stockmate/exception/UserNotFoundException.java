package com.acoldbottle.stockmate.exception;

public class UserNotFoundException extends StockMateException{
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }

    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
