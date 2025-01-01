package com.example.bank.client;

import com.example.bank.client.model.Address;
import com.example.bank.client.model.Role;
import com.example.bank.client.model.Status;

import java.time.LocalDate;

public record ClientDTO(Long id, String firstname, String lastname, LocalDate birthDate, String email, Address address,
                        Role role, Status status, String createdOn, Long createdBy) {
}
