package com.example.bank.userRegister;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Builder
@ToString
class UserRequest {
    private final String firstName;
    private final String lastName;
    private final LocalDate birthDate;
    private final Address address;
    private final String email;
}
