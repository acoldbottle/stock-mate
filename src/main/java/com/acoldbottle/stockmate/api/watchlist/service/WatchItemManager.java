package com.acoldbottle.stockmate.api.watchlist.service;

import com.acoldbottle.stockmate.api.sse.watchlist.WatchlistSubscriberRegistry;
import com.acoldbottle.stockmate.api.trackedsymbol.service.TrackedSymbolService;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.domain.watchitem.WatchItem;
import com.acoldbottle.stockmate.domain.watchitem.WatchItemRepository;
import com.acoldbottle.stockmate.exception.watchitem.WatchItemAlreadyExistsException;
import com.acoldbottle.stockmate.exception.watchitem.WatchItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.acoldbottle.stockmate.exception.ErrorCode.WATCH_ITEM_ALREADY_EXISTS;
import static com.acoldbottle.stockmate.exception.ErrorCode.WATCH_ITEM_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class WatchItemManager {

    private final WatchItemRepository watchItemRepository;
    private final WatchlistSubscriberRegistry subscriberRegistry;
    private final TrackedSymbolService trackedSymbolService;

    public List<WatchItem> getWatchlist(User user) {
        return watchItemRepository.findAllWithStockByUser(user);
    }

    public WatchItem create(User user, Stock stock) {
        boolean exists = watchItemRepository.existsByUserAndStock(user, stock);
        if (exists) {
            throw new WatchItemAlreadyExistsException(WATCH_ITEM_ALREADY_EXISTS);
        }

        WatchItem savedWatchItem = watchItemRepository.save(
                WatchItem.builder()
                        .user(user)
                        .stock(stock)
                        .build());

        subscriberRegistry.register(user.getId(), stock.getSymbol());
        trackedSymbolService.saveTrackedSymbolIfNotExists(stock.getSymbol(), stock.getMarketCode());

        return savedWatchItem;
    }

    public void delete(Long watchItemId, User user) {
        WatchItem watchItem = watchItemRepository.findByIdAndUser(watchItemId, user)
                .orElseThrow(() -> new WatchItemNotFoundException(WATCH_ITEM_NOT_FOUND));

        watchItemRepository.delete(watchItem);
        subscriberRegistry.unregister(user.getId(), watchItem.getStock().getSymbol());
        trackedSymbolService.saveTrackedSymbolIfNotExists(watchItem.getStock().getSymbol(), watchItem.getStock().getMarketCode());
    }
}
