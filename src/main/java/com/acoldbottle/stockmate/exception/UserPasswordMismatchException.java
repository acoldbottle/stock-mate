package com.acoldbottle.stockmate.exception;

public class UserPasswordMismatchException extends StockMateException {

    public UserPasswordMismatchException() {
        super(ErrorCode.USER_PASSWORD_MISMATCH);
    }

    public UserPasswordMismatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
