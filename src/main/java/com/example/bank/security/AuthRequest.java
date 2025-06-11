package com.example.bank.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @Email(message = "invalid email address")
        @NotBlank(message = "email is mandatory")
        String email,

        @NotBlank(message = "password is mandatory")
        String password) {
}
