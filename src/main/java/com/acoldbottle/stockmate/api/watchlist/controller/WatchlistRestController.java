package com.acoldbottle.stockmate.api.watchlist.controller;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.watchlist.dto.req.WatchItemCreateReq;
import com.acoldbottle.stockmate.api.watchlist.dto.res.WatchItemCreateRes;
import com.acoldbottle.stockmate.api.watchlist.service.WatchlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/watchlist")
public class WatchlistRestController {

    private final WatchlistService watchlistService;

    /**
     * 관심 종목 추가
     * 관심 종목 삭제
     * 관심 종목 조회(=리스트)
     */
    @PostMapping
    public ResponseEntity<WatchItemCreateRes> createWatchItem(@UserId Long userId,
                                                              @RequestBody @Valid WatchItemCreateReq watchItemCreateReq) {
        WatchItemCreateRes res = watchlistService.createWatchItem(userId, watchItemCreateReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}
