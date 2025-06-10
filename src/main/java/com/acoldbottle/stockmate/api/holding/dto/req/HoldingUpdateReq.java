package com.acoldbottle.stockmate.api.holding.dto.req;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class HoldingUpdateReq {

    @Positive(message = "수량은 0보다 커야합니다.")
    private int quantity;

    @NotNull(message = "매수가는 필수 입력값 입니다.")
    @Positive(message = "매수가는 0보다 커야합니다.")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal purchasePrice;
}
