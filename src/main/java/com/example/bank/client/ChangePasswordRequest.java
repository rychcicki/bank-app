package com.example.bank.client;

public record ChangePasswordRequest(String currentPassword, String newPassword, String confirmationPassword) {
}
