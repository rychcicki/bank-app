package com.example.bank.userRegister;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

//@AllArgsConstructor - albo to albo Builder()
@Builder
record UserRequest(String firstName, String lastName, LocalDate birthDate, Address address, String email) {
}
