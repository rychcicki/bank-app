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
    ACCOUNT_NOT_FOUND_EXCEPTION("003", "Account not found in database", HttpStatus.NOT_FOUND),
    INVALID_ACCOUNT_NUMBER_EXCEPTION("004", "Invalid account number", HttpStatus.BAD_REQUEST),
    INVALID_MAJORITY_EXCEPTION("005", "Client has to be adult", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST_EXCEPTION("006", "Request validation failed", HttpStatus.BAD_REQUEST),
    CLIENT_ALREADY_EXISTS_EXCEPTION("007", "Email already exists", HttpStatus.CONFLICT),
    INVALID_JWT_EXCEPTION("008", "Invalid token", HttpStatus.UNAUTHORIZED),
    WRONG_PASSWORD_EXCEPTION("009", "Wrong password", HttpStatus.UNAUTHORIZED),
    PASSWORD_NOT_MATCHING_EXCEPTION("010", "Passwords are not the same", HttpStatus.UNAUTHORIZED);

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;
}
