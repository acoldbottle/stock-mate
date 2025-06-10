package com.acoldbottle.stockmate.exception.holding;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.StockMateException;

public class HoldingNotFoundException extends StockMateException {
    public HoldingNotFoundException() {
        super(ErrorCode.HOLDING_NOT_FOUND);
    }

    public HoldingNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
