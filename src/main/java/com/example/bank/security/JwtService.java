package com.example.bank.security;

import jakarta.servlet.http.HttpServletRequest;

public interface JwtService {
    AuthResponse authenticate(AuthRequest request);

    AuthResponse refreshToken(HttpServletRequest request);

    Boolean isTokenValid(String token);
}
