package com.example.bank.client;

import com.example.bank.client.model.Address;

import java.time.LocalDate;

public record ClientRequest(String firstname, String lastname, LocalDate birthDate, String email, Address address,
                            String password) {
}
