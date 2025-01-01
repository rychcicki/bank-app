package com.example.bank.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.example.bank.security.JwtServiceUtils.extractBearerToken;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutServiceImpl implements LogoutHandler, LogoutSuccessHandler {
    private final JwtServiceImpl authService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
        final String token = extractBearerToken(request);
        if (!token.isBlank() && Boolean.TRUE.equals(authService.isTokenValid(token))) {
            authService.revokeToken(token);
        }
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
        SecurityContextHolder.clearContext();
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        try {
            response.getWriter().write("{\"message\":\"You have been logged out successfully\"}");
            response.getWriter().flush();
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
}
