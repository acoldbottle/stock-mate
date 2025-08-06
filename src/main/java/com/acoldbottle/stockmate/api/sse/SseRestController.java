package com.acoldbottle.stockmate.api.sse;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.sse.portfolio.PortfolioSseService;
import com.acoldbottle.stockmate.api.sse.watchlist.WatchlistSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseRestController {

    private final WatchlistSseService watchlistSseService;
    private final PortfolioSseService portfolioSseService;

    @GetMapping(value = "/connect/portfolio", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connectPortfolio(@UserId Long userId) {
        return portfolioSseService.connect(userId);
    }

    @PostMapping("/disconnect/portfolio")
    public void disconnectPortfolio(@UserId Long userId) {
        portfolioSseService.disconnect(userId);
    }

    @GetMapping(value = "/connect/watchlist", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connectWatchlist(@UserId Long userId) {
        return watchlistSseService.connect(userId);
    }

    @PostMapping("/disconnect/watchlist")
    public void disconnectWatchlist(@UserId Long userId) {
        watchlistSseService.disconnect(userId);
    }
}
