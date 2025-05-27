package com.acoldbottle.stockmate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StockMateException.class)
    public ResponseEntity<ErrorResponse> stockMateException(StockMateException e) {
        log.error("[StockMateException] -> {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(e));
    }
}
