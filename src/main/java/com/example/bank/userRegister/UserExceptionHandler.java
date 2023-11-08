package com.example.bank.userRegister;

import jakarta.annotation.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
class UserExceptionHandler {
    /**
     * Czy ta klasa też ma być objęta testami, bo JACOCO wariuje...
     */
    @ExceptionHandler(
            {UserNotFoundException.class, IllegalArgumentException.class}
    )

    final ResponseEntity<List<String>> handleException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof UserNotFoundException) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            UserNotFoundException exception = (UserNotFoundException) ex;
            return handleUserNotFoundException(exception, headers, status, request);
        } else if (ex instanceof IllegalArgumentException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            IllegalArgumentException exception = (IllegalArgumentException) ex;
            return handleIllegalArgumentException(exception, headers, status, request);
        } else {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleInternalException(ex, null, headers, status, request);
        }
    }

    ResponseEntity<List<String>> handleUserNotFoundException(UserNotFoundException exception, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        List<String> errors = Collections.singletonList(exception.getMessage());
        return handleInternalException(exception, errors, headers, status, request);
    }


    ResponseEntity<List<String>> handleIllegalArgumentException(IllegalArgumentException exception, HttpHeaders headers,
                                                                HttpStatus status, WebRequest request) {
        // to samo, co w handleUserNotFoundException
        List<String> errors = Collections.singletonList(exception.getMessage());
        return handleInternalException(exception, errors, headers, status, request);
    }

    ResponseEntity<List<String>> handleInternalException(Exception ex, @Nullable List<String> errors, HttpHeaders headers,
                                                         HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        return new ResponseEntity<>(errors, headers, status);
    }
}
