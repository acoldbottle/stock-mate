package com.acoldbottle.stockmate.exception.kis;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.StockMateException;

public class KisTooManyRequestException extends StockMateException {
    public KisTooManyRequestException() {
        super(ErrorCode.KIS_TOO_MANY_REQUEST);
    }

    public KisTooManyRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
