package com.acoldbottle.stockmate.api.watchlist.controller;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.watchlist.dto.req.WatchItemCreateReq;
import com.acoldbottle.stockmate.api.watchlist.dto.res.WatchItemCreateRes;
import com.acoldbottle.stockmate.api.watchlist.dto.res.WatchItemGetRes;
import com.acoldbottle.stockmate.api.watchlist.service.WatchlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/watchlist")
public class WatchlistRestController implements WatchlistAPI{

    private final WatchlistService watchlistService;

    @GetMapping
    public ResponseEntity<List<WatchItemGetRes>> getWatchlist(@UserId Long userId) {
        List<WatchItemGetRes> res = watchlistService.getWatchlist(userId);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping
    public ResponseEntity<WatchItemCreateRes> createWatchItem(@UserId Long userId,
                                                              @RequestBody @Valid WatchItemCreateReq watchItemCreateReq) {
        WatchItemCreateRes res = watchlistService.createWatchItem(userId, watchItemCreateReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @DeleteMapping("/{watchItemId}")
    public ResponseEntity<Void> deleteWatchItem(@UserId Long userId,
                                                @PathVariable Long watchItemId) {
        watchlistService.deleteWatchItem(userId, watchItemId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
