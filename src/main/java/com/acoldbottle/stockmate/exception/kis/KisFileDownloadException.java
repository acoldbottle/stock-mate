package com.acoldbottle.stockmate.exception.kis;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.StockMateException;

public class KisFileDownloadException extends StockMateException {
    public KisFileDownloadException() {
        super(ErrorCode.KIS_FILE_DOWNLOAD_ERROR);
    }

    public KisFileDownloadException(ErrorCode errorCode) {
        super(errorCode);
    }
}
