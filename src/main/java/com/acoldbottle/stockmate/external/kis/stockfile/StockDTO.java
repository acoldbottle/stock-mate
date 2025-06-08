package com.acoldbottle.stockmate.external.kis.stockfile;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockDTO {

    private String symbol;

    private String korName;

    private String engName;

    private String marketCode;
}
