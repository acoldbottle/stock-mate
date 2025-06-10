package com.acoldbottle.stockmate.exception.stock;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.StockMateException;

public class StockNotFoundException extends StockMateException {
    public StockNotFoundException() {
        super(ErrorCode.STOCK_NOT_FOUND);
    }

    public StockNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
