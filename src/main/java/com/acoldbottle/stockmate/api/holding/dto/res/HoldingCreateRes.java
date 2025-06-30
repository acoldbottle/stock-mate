package com.acoldbottle.stockmate.api.holding.dto.res;

import com.acoldbottle.stockmate.domain.holding.Holding;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class HoldingCreateRes {

    private Long holdingId;
    private String symbol;
    private int quantity;
    private BigDecimal purchasePrice;

    public static HoldingCreateRes from(Holding holding) {
        return HoldingCreateRes.builder()
                .holdingId(holding.getId())
                .symbol(holding.getStock().getSymbol())
                .quantity(holding.getQuantity())
                .purchasePrice(holding.getPurchasePrice())
                .build();
    }
}
