package com.acoldbottle.stockmate.api.holding.controller;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.holding.dto.req.HoldingCreateReq;
import com.acoldbottle.stockmate.api.holding.dto.req.HoldingUpdateReq;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingCreateRes;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingUpdateRes;
import com.acoldbottle.stockmate.api.holding.service.HoldingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
public class HoldingRestController {

    private final HoldingService holdingService;

    /**
     * @GetMapping("/portfolios/{portfolio_id}/stocks -> 포트폴리오 내에 있는 주식들 조회 -> 조회시 주식들의 현재가들도 요청해서 반환
     * @PostMapping("/portfolios/{portfolio_id}/stocks -> 포트폴리오에 보유 주식 추가(=holding) -> 이미 존재하는 주식일 경우 수량이랑 매수가 계산
     * @PatchMapping("/portfolios/{portfolio_id}/stocks/{holding_id} -> 포트폴리오에 보유 주식 수정 -> 수량, 매수가 계산
     * @DeleteMapping("/portfolios/{portfolio_id}/stocks/{holding_id} -> 포트폴리오에 보유 주식 삭제
     */

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
