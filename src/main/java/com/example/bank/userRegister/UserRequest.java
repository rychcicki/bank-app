package com.example.bank.userRegister;

import java.time.LocalDate;

record UserRequest(String firstName, String lastName, LocalDate birthDate, Address address, String email) {
}
