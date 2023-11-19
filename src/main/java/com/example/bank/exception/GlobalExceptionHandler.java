package com.example.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Map<Class<? extends Exception>, Function<Exception, ResponseEntity<ApiError>>>
            exceptionHandlers;

    public GlobalExceptionHandler() {
        this.exceptionHandlers = new HashMap<>();
        this.exceptionHandlers.put(ClientNotFoundException.class, this::handleClientNotFoundException);
        this.exceptionHandlers.put(IllegalArgumentException.class, this::handleIllegalArgumentException);
    }

    @ExceptionHandler({ClientNotFoundException.class, IllegalArgumentException.class, RuntimeException.class})
    public ResponseEntity<ApiError> handleException(Exception ex) {
        Function<Exception, ResponseEntity<ApiError>> handler = exceptionHandlers.getOrDefault(ex.getClass(),
                this::handleInternalException);
        return handler.apply(ex);
    }

    private ResponseEntity<ApiError> handleClientNotFoundException(Exception ex) {
        ClientNotFoundException exception = (ClientNotFoundException) ex;
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiError apiError = new ApiError(status, exception);
        return new ResponseEntity<>(apiError, status);
    }

    private ResponseEntity<ApiError> handleIllegalArgumentException(Exception ex) {
        IllegalArgumentException exception = (IllegalArgumentException) ex;
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError apiError = new ApiError(status, exception);
        return new ResponseEntity<>(apiError, status);
    }

    private ResponseEntity<ApiError> handleInternalException(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiError apiError = new ApiError(status, ex);
        return new ResponseEntity<>(apiError, status);
    }
}
