package com.example.bank.security;

public interface JwtService {
    AuthResponse authenticate(AuthRequest request);

    AuthResponse refreshToken(String authHeader);

    boolean isTokenValid(String token);
}
