package com.example.bank.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ApiError {
    private final HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MMM-YYYY HH:mm:ss")
    private final LocalDateTime timestamp;
    private final String message;

    ApiError(HttpStatus status, Exception ex) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = ex.getLocalizedMessage();
    }
}
