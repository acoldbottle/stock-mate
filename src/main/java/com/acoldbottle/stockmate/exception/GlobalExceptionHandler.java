package com.acoldbottle.stockmate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> notValidException(MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();
        String errorMessage = bindingResult.getFieldError().getDefaultMessage();
        log.error("[MethodArgumentNotValidException] -> {}", errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage));
    }

    @ExceptionHandler(StockMateException.class)
    public ResponseEntity<ErrorResponse> stockMateException(StockMateException e) {
        log.error("[StockMateException] -> {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(e));
    }
}
