package com.acoldbottle.stockmate.exception.kis;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.StockMateException;

public class KisCurrentPriceException extends StockMateException {
    public KisCurrentPriceException() {
        super(ErrorCode.KIS_CURRENT_PRICE_ERROR);
    }

    public KisCurrentPriceException(ErrorCode errorCode) {
        super(errorCode);
    }
}
