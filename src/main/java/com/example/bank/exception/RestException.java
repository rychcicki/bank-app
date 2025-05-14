package com.example.bank.exception;

import lombok.Getter;

@Getter
public class RestException extends RuntimeException {
    private final ExceptionType exceptionType;

    public RestException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

    public RestException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType.getMessage(), cause);
        this.exceptionType = exceptionType;
    }
}
