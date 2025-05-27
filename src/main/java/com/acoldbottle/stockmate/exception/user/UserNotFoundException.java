package com.acoldbottle.stockmate.exception.user;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.StockMateException;

public class UserNotFoundException extends StockMateException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }

    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
