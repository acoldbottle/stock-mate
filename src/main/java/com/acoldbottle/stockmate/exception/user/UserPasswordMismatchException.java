package com.acoldbottle.stockmate.exception.user;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.StockMateException;

public class UserPasswordMismatchException extends StockMateException {

    public UserPasswordMismatchException() {
        super(ErrorCode.USER_PASSWORD_MISMATCH);
    }

    public UserPasswordMismatchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
