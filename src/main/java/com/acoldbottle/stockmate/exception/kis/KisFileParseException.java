package com.acoldbottle.stockmate.exception.kis;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.StockMateException;

public class KisFileParseException extends StockMateException {
    public KisFileParseException() {
        super(ErrorCode.KIS_FILE_PARSE_ERROR);
    }

    public KisFileParseException(ErrorCode errorCode) {
        super(errorCode);
    }
}
