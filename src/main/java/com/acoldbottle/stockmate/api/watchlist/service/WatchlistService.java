package com.acoldbottle.stockmate.api.watchlist.service;

import com.acoldbottle.stockmate.api.sse.SubscriberRegistry;
import com.acoldbottle.stockmate.api.trackedsymbol.service.TrackedSymbolService;
import com.acoldbottle.stockmate.api.watchlist.dto.req.WatchItemCreateReq;
import com.acoldbottle.stockmate.api.watchlist.dto.res.WatchItemCreateRes;
import com.acoldbottle.stockmate.api.watchlist.dto.res.WatchItemGetRes;
import com.acoldbottle.stockmate.api.currentprice.dto.CurrentPriceDTO;
import com.acoldbottle.stockmate.api.currentprice.service.CurrentPriceCacheService;
import com.acoldbottle.stockmate.domain.stock.Stock;
import com.acoldbottle.stockmate.domain.stock.StockRepository;
import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.domain.user.UserRepository;
import com.acoldbottle.stockmate.domain.watchitem.WatchItem;
import com.acoldbottle.stockmate.domain.watchitem.WatchItemRepository;
import com.acoldbottle.stockmate.exception.stock.StockNotFoundException;
import com.acoldbottle.stockmate.exception.user.UserNotFoundException;
import com.acoldbottle.stockmate.exception.watchitem.WatchItemAlreadyExistsException;
import com.acoldbottle.stockmate.exception.watchitem.WatchItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.acoldbottle.stockmate.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchlistService {

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final WatchItemRepository watchItemRepository;
    private final TrackedSymbolService trackedSymbolService;
    private final SubscriberRegistry subscriberRegistry;
    private final CurrentPriceCacheService currentPriceCacheService;

    public List<WatchItemGetRes> getWatchlist(Long userId) {
        User user = getUser(userId);
        List<WatchItem> watchlist = watchItemRepository.findAllWithStockByUser(user);

        return watchlist.stream()
                .map(watchItem -> {
                    CurrentPriceDTO currentPrice = currentPriceCacheService.getCurrentPrice(watchItem.getStock().getSymbol());
                    return WatchItemGetRes.from(watchItem, currentPrice);
                })
                .toList();
    }

    @Transactional
    public WatchItemCreateRes createWatchItem(Long userId, WatchItemCreateReq watchItemCreateReq) {
        User user = getUser(userId);
        Stock stock = getStock(watchItemCreateReq.getSymbol());
        boolean exists = watchItemRepository.existsByUserAndStock(user, stock);
        if (exists) {
            throw new WatchItemAlreadyExistsException(WATCH_ITEM_ALREADY_EXISTS);
        }
        WatchItem savedWatchItem = watchItemRepository.save(WatchItem.builder()
                .user(user)
                .stock(stock)
                .build());

        trackedSymbolService.saveTrackedSymbolIfNotExists(stock.getSymbol(), stock.getMarketCode());
        subscriberRegistry.save(userId, stock.getSymbol());
        return WatchItemCreateRes.from(savedWatchItem);
    }

    @Transactional
    public void deleteWatchItem(Long userId, Long watchItemId) {
        User user = getUser(userId);
        WatchItem watchItem = getWatchItem(watchItemId, user);
        watchItemRepository.delete(watchItem);
        trackedSymbolService.deleteTrackedSymbolIfNotUse(watchItem.getStock());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    private Stock getStock(String symbol) {
        return stockRepository.findById(symbol)
                .orElseThrow(() -> new StockNotFoundException(STOCK_NOT_FOUND));
    }

    private WatchItem getWatchItem(Long watchItemId, User user) {
        return watchItemRepository.findByIdAndUser(watchItemId, user)
                .orElseThrow(() -> new WatchItemNotFoundException(WATCH_ITEM_NOT_FOUND));
    }
}
