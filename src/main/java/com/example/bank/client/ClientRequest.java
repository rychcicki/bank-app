package com.example.bank.client;

import com.example.bank.client.jpa.Address;

import java.time.LocalDate;

public record ClientRequest(String firstName, String lastName, LocalDate birthDate, String email, Address address,
                            String password, Role role) {
}
