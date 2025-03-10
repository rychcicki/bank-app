package com.example.bank.exception;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RestException.class)
    public ResponseEntity<ErrorResponse> handleException(RestException ex) {
        ExceptionType exceptionType = ex.getExceptionType();
        ErrorResponse errorResponse = new ErrorResponse(exceptionType.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(exceptionType.getHttpStatus()).body(errorResponse);
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(RequestNotPermitted.class)
    public void rateLimitExceeded() {
    }
}
