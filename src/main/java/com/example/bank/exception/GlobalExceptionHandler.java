package com.example.bank.exception;

import jakarta.annotation.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import java.util.Collections;
import java.util.List;

@RestControllerAdvice
/** 1) Nie widzę różnicy między @RestControllerAdvice i @ControllerAdvice.
 * Teoretycznie teraz powinienem dostać odpowiedź JSON lub XLM, ale nie zauważyłem różnicy w odpowiedzi z serwera.
 *
 * 2) Swoją drogą, skoro zwracana wartość ma być web response, to czy powinienem wrapować poniższe metody
 *   w ResponseEntity<> ?? W controllerze też ustawiam statusy.
 *
 * 3) A po trzecie, czy ktoś te statusy na froncie w ogóle czyta?  </>*/
public class GlobalExceptionHandler {
    @ExceptionHandler(
            {ClientNotFoundException.class, IllegalArgumentException.class}
    )

    final ResponseEntity<List<String>> handleException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        String exceptionTypeString = ex.getClass().getSimpleName();
        switch (exceptionTypeString) {
            case ("ClientNotFoundException") -> {
                HttpStatus status = HttpStatus.NOT_FOUND;
                ClientNotFoundException exception = (ClientNotFoundException) ex;
                return handleClientNotFoundException(exception, headers, status, request);
            }
            case ("IllegalArgumentException") -> {
                HttpStatus status = HttpStatus.BAD_REQUEST;
                IllegalArgumentException exception = (IllegalArgumentException) ex;
                return handleIllegalArgumentException(exception, headers, status, request);
            }
            default -> {
                HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                return handleInternalException(ex, null, headers, status, request);
                /** 4) Chyba powyższą linię zmienialiśmy pod kątem tego pola null, ale nie potrafię odnaleźć tego kodu.*/
            }
        }
    }

    ResponseEntity<List<String>> handleClientNotFoundException(ClientNotFoundException exception, HttpHeaders headers,
                                                               HttpStatus status, WebRequest request) {
        List<String> errors = Collections.singletonList(exception.getMessage());
        return handleInternalException(exception, errors, headers, status, request);
    }


    ResponseEntity<List<String>> handleIllegalArgumentException(IllegalArgumentException exception, HttpHeaders headers,
                                                                HttpStatus status, WebRequest request) {
        List<String> errors = Collections.singletonList(exception.getMessage());
        return handleInternalException(exception, errors, headers, status, request);
    }

    ResponseEntity<List<String>> handleInternalException(Exception ex, @Nullable List<String> errors,
                                                         HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        return new ResponseEntity<>(errors, headers, status);
    }
}
