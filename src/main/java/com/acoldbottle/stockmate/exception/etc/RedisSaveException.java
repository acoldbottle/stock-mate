package com.acoldbottle.stockmate.exception.etc;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.StockMateException;

public class RedisSaveException extends StockMateException {

    public RedisSaveException() {
        super(ErrorCode.REDIS_SAVE_ERROR);
    }

    public RedisSaveException(ErrorCode errorCode) {
        super(errorCode);
    }
}
