package com.acoldbottle.stockmate.api.watchlist.service;

import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.api.currentprice.service.CurrentPriceCacheService;
import com.acoldbottle.stockmate.api.stock.service.StockManager;
import com.acoldbottle.stockmate.api.user.service.UserManager;
import com.acoldbottle.stockmate.api.watchlist.dto.req.WatchItemCreateReq;
import com.acoldbottle.stockmate.api.watchlist.dto.res.WatchItemCreateRes;
import com.acoldbottle.stockmate.api.watchlist.dto.res.WatchItemGetRes;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.domain.watchitem.WatchItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchlistService {

    private final WatchItemManager watchItemManager;
    private final UserManager userManager;
    private final StockManager stockManager;
    private final CurrentPriceCacheService cacheService;

    public List<WatchItemGetRes> getWatchlist(Long userId) {
        User user = userManager.get(userId);

        List<WatchItem> watchlist = watchItemManager.getWatchlist(user.getId());
        return watchlist.stream()
                .map(watchItem -> {
                    CurrentPriceDTO currentPrice = cacheService.getCurrentPrice(watchItem.getStock().getSymbol());
                    return WatchItemGetRes.from(watchItem, currentPrice);
                })
                .toList();
    }

    @Transactional
    public WatchItemCreateRes createWatchItem(Long userId, WatchItemCreateReq watchItemCreateReq) {
        User user = userManager.get(userId);
        Stock stock = stockManager.get(watchItemCreateReq.getSymbol());

        WatchItem savedWatchItem = watchItemManager.create(user, stock);
        return WatchItemCreateRes.from(savedWatchItem);
    }

    @Transactional
    public void deleteWatchItem(Long userId, Long watchItemId) {
        User user = userManager.get(userId);
        watchItemManager.delete(watchItemId, user);
    }
}
