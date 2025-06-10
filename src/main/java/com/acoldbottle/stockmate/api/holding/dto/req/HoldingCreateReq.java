package com.acoldbottle.stockmate.api.holding.dto.req;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class HoldingCreateReq {

    @NotBlank(message = "symbol의 값이 비어있습니다.")
    private String symbol;

    @Positive(message = "수량은 0보다 커야합니다.")
    private int quantity;

    @NotNull
    @Positive(message = "매수가는 0보다 커야합니다.")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal purchasePrice;
}
