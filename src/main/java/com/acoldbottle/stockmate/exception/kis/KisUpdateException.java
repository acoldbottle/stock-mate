package com.acoldbottle.stockmate.exception.kis;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.StockMateException;

public class KisUpdateException extends StockMateException {
    public KisUpdateException() {
        super(ErrorCode.KIS_UPDATE_ERROR);
    }

    public KisUpdateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
