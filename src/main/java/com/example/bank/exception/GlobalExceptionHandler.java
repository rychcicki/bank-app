package com.example.bank.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RestException.class)
    public ResponseEntity<ErrorResponse> handleException(RestException ex) {
        ExceptionType exceptionType = ex.getExceptionType();
        ErrorResponse errorResponse = new ErrorResponse(exceptionType.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(exceptionType.getHttpStatus()).body(errorResponse);
    }
}
