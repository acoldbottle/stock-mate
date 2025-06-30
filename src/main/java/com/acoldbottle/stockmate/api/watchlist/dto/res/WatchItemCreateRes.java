package com.acoldbottle.stockmate.api.watchlist.dto.res;

import com.acoldbottle.stockmate.domain.watchitem.WatchItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WatchItemCreateRes {

    private Long watchItemId;
    private String symbol;

    public static WatchItemCreateRes from(WatchItem watchItem) {
        return WatchItemCreateRes.builder()
                .watchItemId(watchItem.getId())
                .symbol(watchItem.getStock().getSymbol())
                .build();
    }
}
