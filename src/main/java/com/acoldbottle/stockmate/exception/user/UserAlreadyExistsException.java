package com.acoldbottle.stockmate.exception.user;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.StockMateException;

public class UserAlreadyExistsException extends StockMateException {
    public UserAlreadyExistsException() {
        super(ErrorCode.USER_ALREADY_EXISTS);
    }

    public UserAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
