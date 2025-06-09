package com.acoldbottle.stockmate.api.stock.dto.res;


import com.acoldbottle.stockmate.domain.stock.Stock;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockSearchRes {

    private String symbol;
    private String korName;
    private String engName;
    private String marketCode;

    public static StockSearchRes from(Stock stock) {
        return StockSearchRes.builder()
                .symbol(stock.getSymbol())
                .korName(stock.getKorName())
                .engName(stock.getEngName())
                .marketCode(stock.getMarketCode())
                .build();
    }
}
