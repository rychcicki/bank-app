package com.example.bank.client;

import com.example.bank.client.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ClientUpdateRequest(
        @NotBlank(message = "firstname is mandatory")
        String firstname,

        @NotBlank(message = "lastname is mandatory")
        String lastname,

        @NotNull(message = "birth date is mandatory")
        LocalDate birthDate,

        @Email(message = "invalid email address")
        @NotBlank(message = "email is mandatory")
        String email,

        @Valid
        Address address) {
}
