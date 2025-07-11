package com.acoldbottle.stockmate.api.stock.controller;

import com.acoldbottle.stockmate.api.stock.dto.res.StockSearchRes;
import com.acoldbottle.stockmate.api.stock.service.StockService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stocks")
public class StockRestController implements StockAPI{

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockSearchRes>> searchStocks(@RequestParam @NotBlank String keyword) {
        List<StockSearchRes> stockList = stockService.searchByKeyword(keyword);
        return ResponseEntity.status(HttpStatus.OK).body(stockList);
    }
}
