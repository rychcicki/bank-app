package com.example.bank.exception;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(RestException.class)
    public ResponseEntity<ErrorResponse> handleRestException(RestException ex) {
        ExceptionType exceptionType = ex.getExceptionType();
        if (ex.getCause() != null) {
            log.error("Handled RestException: {}, root cause:  {}", exceptionType, ex.getCause().getMessage());
        }
        return buildErrorResponse(exceptionType, ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HandlerMethodValidationException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(Exception ex) {
        return buildErrorResponse(ExceptionType.INVALID_REQUEST_EXCEPTION, extractValidationMessage(ex));
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(RequestNotPermitted.class)
    public void rateLimitExceeded() {
    }

    private static ResponseEntity<ErrorResponse> buildErrorResponse(ExceptionType exceptionType, String message) {
        ErrorResponse errorResponse = new ErrorResponse(exceptionType.getErrorCode(), message);
        return ResponseEntity.status(exceptionType.getHttpStatus()).body(errorResponse);
    }

    private static String extractValidationMessage(Exception ex) {
        return switch (ex) {
            case MethodArgumentNotValidException methodArgNotValidEx ->
                    Optional.ofNullable(methodArgNotValidEx.getBindingResult().getFieldError())
                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                            .orElse(ExceptionType.INVALID_REQUEST_EXCEPTION.getMessage());

            case HandlerMethodValidationException handlerMethodValidationEx ->
                    handlerMethodValidationEx.getParameterValidationResults().stream()
                            .flatMap(errors -> errors.getResolvableErrors().stream())
                            .map(MessageSourceResolvable::getDefaultMessage)
                            .filter(msg -> msg != null && !msg.isBlank())
                            .findFirst()
                            .orElse(ExceptionType.INVALID_REQUEST_EXCEPTION.getMessage());
            default -> ExceptionType.INVALID_REQUEST_EXCEPTION.getMessage();
        };
    }
}
