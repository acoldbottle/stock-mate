package com.acoldbottle.stockmate.api.portfolio.controller;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.portfolio.dto.PortfolioCreateReq;
import com.acoldbottle.stockmate.api.portfolio.dto.PortfolioCreateRes;
import com.acoldbottle.stockmate.api.portfolio.dto.PortfolioGetRes;
import com.acoldbottle.stockmate.api.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PortfolioRestController {

    private final PortfolioService portfolioService;

    @PostMapping("/portfolio")
    public ResponseEntity<PortfolioCreateRes> createPortfolio(@UserId Long userId,
                                                              @RequestBody @Valid PortfolioCreateReq portfolioCreateReq) {
        PortfolioCreateRes portfolioCreateRes = portfolioService.createPortfolio(userId, portfolioCreateReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(portfolioCreateRes);
    }

    @GetMapping("/portfolio")
    public ResponseEntity<List<PortfolioGetRes>> getPortfolioList(@UserId Long userId) {
        List<PortfolioGetRes> list = portfolioService.getPortfolioList(userId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

}
