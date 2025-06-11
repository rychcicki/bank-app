package com.example.bank.security;

import com.example.bank.client.ClientDTO;
import com.example.bank.client.ClientRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
class AuthController {
    private final JwtServiceImpl authService;

    @PostMapping("/register")
    ClientDTO register(@RequestBody @Valid ClientRequest clientRequest) {
        return authService.registerClient(clientRequest);
    }

    @PostMapping("/login")
    AuthResponse login(@RequestBody AuthRequest request) {
        return authService.authenticate(request);
    }

    @PostMapping("/refresh-token")
    AuthResponse refreshToken(@RequestHeader("Authorization") String authHeader) {
        return authService.refreshToken(authHeader);
    }

    @PatchMapping("/change-password")
    void changePassword(@RequestBody @Valid ChangePasswordRequest request, Principal connectedUser) {
        authService.changePassword(request, connectedUser);
    }
}
