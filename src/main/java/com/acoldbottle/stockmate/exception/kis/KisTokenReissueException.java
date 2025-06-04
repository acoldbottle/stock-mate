package com.acoldbottle.stockmate.exception.kis;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.StockMateException;

public class KisTokenReissueException extends StockMateException {
    public KisTokenReissueException() {
        super(ErrorCode.KIS_TOKEN_REISSUE_FAILED);
    }

    public KisTokenReissueException(ErrorCode errorCode) {
        super(errorCode);
    }
}
