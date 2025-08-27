package com.acoldbottle.stockmate.api.watchlist.manager;

import com.acoldbottle.stockmate.event.watchlist.WatchlistSubscriberEvent;
import com.acoldbottle.stockmate.api.trackedsymbol.manager.TrackedSymbolManager;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.domain.watchitem.WatchItem;
import com.acoldbottle.stockmate.domain.watchitem.WatchItemRepository;
import com.acoldbottle.stockmate.exception.watchitem.WatchItemAlreadyExistsException;
import com.acoldbottle.stockmate.exception.watchitem.WatchItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.acoldbottle.stockmate.event.watchlist.WatchlistSubscriberEvent.WatchlistEventType.*;
import static com.acoldbottle.stockmate.exception.ErrorCode.WATCH_ITEM_ALREADY_EXISTS;
import static com.acoldbottle.stockmate.exception.ErrorCode.WATCH_ITEM_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class WatchItemManager {

    private final WatchItemRepository watchItemRepository;
    private final TrackedSymbolManager trackedSymbolManager;
    private final ApplicationEventPublisher eventPublisher;

    public List<WatchItem> getWatchlist(Long userId) {
        return watchItemRepository.findAllWithStockByUserId(userId);
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

        eventPublisher.publishEvent(new WatchlistSubscriberEvent(user.getId(), stock.getSymbol(), CREATE));
        trackedSymbolManager.saveTrackedSymbolIfNotExists(stock.getSymbol(), stock.getMarketCode());

        return savedWatchItem;
    }

    public void delete(Long watchItemId, User user) {
        WatchItem watchItem = watchItemRepository.findByIdAndUser(watchItemId, user.getId())
                .orElseThrow(() -> new WatchItemNotFoundException(WATCH_ITEM_NOT_FOUND));
        watchItemRepository.delete(watchItem);

        eventPublisher.publishEvent(new WatchlistSubscriberEvent(user.getId(), watchItem.getStock().getSymbol(), DELETE));
        trackedSymbolManager.saveTrackedSymbolIfNotExists(watchItem.getStock().getSymbol(), watchItem.getStock().getMarketCode());
    }
}
