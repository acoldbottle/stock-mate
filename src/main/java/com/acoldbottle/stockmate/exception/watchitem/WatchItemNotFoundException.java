package com.acoldbottle.stockmate.exception.watchitem;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.StockMateException;

public class WatchItemNotFoundException extends StockMateException {
    public WatchItemNotFoundException() {
        super(ErrorCode.WATCH_ITEM_NOT_FOUND);
    }

    public WatchItemNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
