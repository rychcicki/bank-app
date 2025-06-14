package com.example.bank.security;

import jakarta.validation.constraints.NotBlank;

record ChangePasswordRequest(
        @NotBlank(message = "old password is mandatory")
        String oldPassword,

        @NotBlank(message = "new password is mandatory")
        String newPassword,

        @NotBlank(message = "password confirmation is mandatory")
        String confirmPassword) {
}
