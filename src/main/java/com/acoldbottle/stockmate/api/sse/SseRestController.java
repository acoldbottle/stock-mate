package com.acoldbottle.stockmate.api.sse;

import com.acoldbottle.stockmate.annotation.UserId;
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

    @GetMapping(value = "/connect/watchlist", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@UserId Long userId) {
        return watchlistSseService.connect(userId);
    }

    @PostMapping("/disconnect/watchlist")
    public void disconnect(@UserId Long userId) {
        watchlistSseService.disconnect(userId);
    }
}
