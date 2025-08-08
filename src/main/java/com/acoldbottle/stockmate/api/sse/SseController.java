package com.acoldbottle.stockmate.api.sse;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.sse.holding.HoldingSseService;
import com.acoldbottle.stockmate.api.sse.portfolio.PortfolioSseService;
import com.acoldbottle.stockmate.api.sse.watchlist.WatchlistSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {

    private final HoldingSseService holdingSseService;
    private final PortfolioSseService portfolioSseService;
    private final WatchlistSseService watchlistSseService;

    @GetMapping("/connect/holding/{portfolioId}")
    public SseEmitter connectHoldingSSE(@UserId Long userId, @PathVariable Long portfolioId) {
        return holdingSseService.connect(userId, portfolioId);
    }

    @PostMapping("/disconnect/holding/{portfolioId}")
    public void disconnectHoldingSSE(@UserId Long userId, @PathVariable Long portfolioId) {
        holdingSseService.disconnect(userId, portfolioId);
    }

    @GetMapping("/connect/portfolio")
    public SseEmitter connectPortfolioSSE(@UserId Long userId) {
        return portfolioSseService.connect(userId);
    }

    @PostMapping("/disconnect/portfolio")
    public void disconnectPortfolioSSE(@UserId Long userId) {
        portfolioSseService.disconnect(userId);
    }

    @GetMapping("/connect/watchlist")
    public SseEmitter connectWatchlist(@UserId Long userId) {
        return watchlistSseService.connect(userId);
    }

    @PostMapping("/disconnect/watchlist")
    public void disconnectWatchlist(@UserId Long userId) {
        watchlistSseService.disconnect(userId);
    }
}
