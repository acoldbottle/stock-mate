package com.acoldbottle.stockmate.api.stock.controller;

import com.acoldbottle.stockmate.api.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock")
public class StockRestController {

    private final StockService stockService;

    @PostMapping("/update")
    public ResponseEntity<Void> updateStockDB() {

        stockService.updateStockDB();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
