package com.example.bank.security;

record ChangePasswordRequest(String currentPassword, String newPassword, String confirmationPassword) {
}
