package com.example.bank.registration;

import com.example.bank.registration.jpa.Address;

import java.time.LocalDate;

record ClientRequest(Long id, String firstName, String lastName, LocalDate birthDate, String email, Address address) {
}
