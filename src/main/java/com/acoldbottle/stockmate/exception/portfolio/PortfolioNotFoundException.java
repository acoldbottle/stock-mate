package com.acoldbottle.stockmate.exception.portfolio;

import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.StockMateException;

public class PortfolioNotFoundException extends StockMateException {
    public PortfolioNotFoundException() {
        super(ErrorCode.PORTFOLIO_NOT_FOUND);
    }

    public PortfolioNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
