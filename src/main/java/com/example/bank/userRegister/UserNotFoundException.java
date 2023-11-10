package com.example.bank.userRegister;

import lombok.extern.slf4j.Slf4j;


@Slf4j
class UserNotFoundException extends RuntimeException {     // nie dziedziczyć po Exception !!!
    public UserNotFoundException(String message) {
        super(message);
    }
}
