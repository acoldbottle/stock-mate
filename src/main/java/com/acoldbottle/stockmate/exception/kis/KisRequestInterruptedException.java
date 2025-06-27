package com.acoldbottle.stockmate.exception.kis;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.StockMateException;

public class KisRequestInterruptedException extends StockMateException {
    public KisRequestInterruptedException() {
        super(ErrorCode.KIS_REQUEST_INTERRUPTED_ERROR);
    }

    public KisRequestInterruptedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
