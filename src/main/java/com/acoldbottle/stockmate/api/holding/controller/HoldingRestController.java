package com.acoldbottle.stockmate.api.holding.controller;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.holding.dto.req.HoldingCreateReq;
import com.acoldbottle.stockmate.api.holding.dto.req.HoldingUpdateReq;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingCreateRes;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingGetWithProfitRes;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingUpdateRes;
import com.acoldbottle.stockmate.api.holding.service.HoldingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
public class HoldingRestController {

    private final HoldingService holdingService;

    @GetMapping("/{portfolioId}/stocks")
    public ResponseEntity<List<HoldingGetWithProfitRes>> getHoldingListWithProfit(@UserId Long userId,
                                                                                  @PathVariable Long portfolioId) {
        List<HoldingGetWithProfitRes> result= holdingService.getHoldingListWithProfit(userId, portfolioId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/{portfolioId}/stocks")
    public ResponseEntity<HoldingCreateRes> createHolding(@UserId Long userId,
                                                          @PathVariable Long portfolioId,
                                                          @RequestBody @Valid HoldingCreateReq holdingCreateReq) {
        HoldingCreateRes holdingCreateRes = holdingService.createHolding(userId, portfolioId, holdingCreateReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(holdingCreateRes);
    }

    @PatchMapping("/{portfolioId}/stocks/{holdingId}")
    public ResponseEntity<HoldingUpdateRes> updateHolding(@UserId Long userId,
                                                          @PathVariable Long portfolioId,
                                                          @PathVariable Long holdingId,
                                                          @RequestBody @Valid HoldingUpdateReq holdingUpdateReq) {
        HoldingUpdateRes holdingUpdateRes = holdingService.updateHolding(userId, portfolioId, holdingId, holdingUpdateReq);
        return ResponseEntity.status(HttpStatus.OK).body(holdingUpdateRes);
    }

    @DeleteMapping("/{portfolioId}/stocks/{holdingId}")
    public ResponseEntity<Void> deleteHolding(@UserId Long userId,
                                              @PathVariable Long portfolioId,
                                              @PathVariable Long holdingId) {
        holdingService.deleteHolding(userId, portfolioId, holdingId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
