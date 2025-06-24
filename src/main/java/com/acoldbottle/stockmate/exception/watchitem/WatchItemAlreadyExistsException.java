package com.acoldbottle.stockmate.exception.watchitem;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.StockMateException;

public class WatchItemAlreadyExistsException extends StockMateException {
    public WatchItemAlreadyExistsException() {
        super(ErrorCode.WATCH_ITEM_ALREADY_EXISTS);
    }

    public WatchItemAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
