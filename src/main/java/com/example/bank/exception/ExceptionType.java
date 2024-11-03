package com.example.bank.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionType {
    CLIENT_NOT_FOUND_EXCEPTION("001", "Client not found in database", HttpStatus.NOT_FOUND),
    XLSX_GENERATING_EXCEPTION("002", "Cannot generate transfer history XLSX file",
            HttpStatus.INTERNAL_SERVER_ERROR),
    ACCOUNT_NOT_FOUND_EXCEPTION("003", "Account not found in database", HttpStatus.NOT_FOUND);

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;
}
