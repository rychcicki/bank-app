package com.example.bank.exception;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String message) {
        super(message);
    }
}
