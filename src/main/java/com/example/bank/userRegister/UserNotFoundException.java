package com.example.bank.userRegister;

import lombok.extern.slf4j.Slf4j;


@Slf4j
class UserNotFoundException extends Exception {
    UserNotFoundException(Long id) {
        log.error("Error. There is no user with id #" + id + " in database.");
    }
}
