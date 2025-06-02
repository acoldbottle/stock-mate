package com.acoldbottle.stockmate.api.portfolio.controller;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.portfolio.dto.req.PortfolioCreateReq;
import com.acoldbottle.stockmate.api.portfolio.dto.req.PortfolioUpdateReq;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioCreateRes;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioGetRes;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioUpdateRes;
import com.acoldbottle.stockmate.api.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PortfolioRestController implements PortfolioAPI{

    private final PortfolioService portfolioService;

    @GetMapping("/portfolio")
    public ResponseEntity<List<PortfolioGetRes>> getPortfolioList(@UserId Long userId) {
        List<PortfolioGetRes> list = portfolioService.getPortfolioList(userId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PostMapping("/portfolio")
    public ResponseEntity<PortfolioCreateRes> createPortfolio(@UserId Long userId,
                                                              @RequestBody @Valid PortfolioCreateReq portfolioCreateReq) {
        PortfolioCreateRes portfolioCreateRes = portfolioService.createPortfolio(userId, portfolioCreateReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(portfolioCreateRes);
    }

    @PatchMapping("/portfolio/{portfolioId}")
    public ResponseEntity<PortfolioUpdateRes> updatePortfolioTitle(@UserId Long userId,
                                                                   @PathVariable Long portfolioId,
                                                                   @RequestBody @Valid PortfolioUpdateReq portfolioUpdateReq) {
        PortfolioUpdateRes portfolioUpdateRes = portfolioService.updatePortfolio(userId, portfolioId, portfolioUpdateReq);
        return ResponseEntity.status(HttpStatus.OK).body(portfolioUpdateRes);
    }

    @DeleteMapping("/portfolio/{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(@UserId Long userId,
                                                @PathVariable @NotNull Long portfolioId) {
        portfolioService.deletePortfolio(userId, portfolioId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
