package com.acoldbottle.stockmate.api.watchlist.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WatchItemCreateReq {

    @NotBlank(message = "symbol의 값이 비어있습니다.")
    private String symbol;
}
