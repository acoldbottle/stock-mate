package com.acoldbottle.stockmate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String errorMessage = bindingResult.getFieldError().getDefaultMessage();
        log.error("[MethodArgumentNotValidException] -> {}", errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage));
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ErrorCode errorCode = ErrorCode.INVALID_TYPE_MISMATCH;
        log.error("[MethodArgumentTypeMismatchException] -> {}", errorCode.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorCode.getHttpStatus(), errorCode.getMessage()));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    protected ResponseEntity<ErrorResponse> handleMethodValidationException(HandlerMethodValidationException e) {
        ErrorCode errorcode = ErrorCode.STOCK_SEARCH_COND;
        log.error("[MissingServletRequestParameterException] -> {}", errorcode.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorcode.getHttpStatus(), errorcode.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleRequestParameterException(MissingServletRequestParameterException e) {
        ErrorCode errorcode = ErrorCode.INVALID_REQUEST_PARAM;
        log.error("[MissingServletRequestParameterException] -> {}", errorcode.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorcode.getHttpStatus(), errorcode.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST_BODY;
        log.error("[HttpMessageNotReadableException] -> {}", errorCode.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorCode.getHttpStatus(), errorCode.getMessage()));
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    protected ResponseEntity<ErrorResponse> handleAuthException(Exception e) {
        String message = "아이디 또는 비밀번호가 잘못되었습니다.";
        log.error("[{}] -> {}", e.getClass().getSimpleName(), message);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED, message));
    }

    @ExceptionHandler(StockMateException.class)
    protected ResponseEntity<ErrorResponse> handleStockMateException(StockMateException e) {
        log.error("[StockMateException] -> {}", e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(e));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorCode error = ErrorCode.INTERNAL_SERVER_ERROR;
        log.error("[{}] -> {}", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity
                .status(error.getHttpStatus())
                .body(new ErrorResponse(error.getHttpStatus(), error.getMessage()));
    }
}
