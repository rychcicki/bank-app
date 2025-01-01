package com.example.bank.security;

import com.example.bank.client.ClientDTO;
import com.example.bank.client.ClientRepository;
import com.example.bank.client.ClientRequest;
import com.example.bank.client.ClientService;
import com.example.bank.client.model.Client;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import com.example.bank.security.model.RevokedToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;

import static com.example.bank.security.JwtServiceUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {
    private final ClientService clientService;
    private final ClientRepository clientRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final Long jwtExpiration = 1000 * 60 * 15L;
    private final Long refreshExpiration = 1000 * 60 * 4L;

    @Value("${age-of-majority}")
    @Getter
    @Setter(AccessLevel.PACKAGE) // only for testing
    private Integer majority;

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    ClientDTO registerClient(ClientRequest clientRequest) {
        return clientService.createClient(clientRequest);
    }

    @Override
    public AuthResponse authenticate(final AuthRequest request) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        final UserDetails userDetails = (UserDetails) authenticate.getPrincipal();
        final String token = buildToken(userDetails.getUsername(), jwtExpiration, secretKey);
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse refreshToken(final HttpServletRequest request) {
        final String token = extractBearerToken(request);
        if (token.isBlank() || Boolean.FALSE.equals(isTokenValid(token))) {
            throw new RestException(ExceptionType.INVALID_JWT_EXCEPTION);
        }
        final String username = extractClaim(token, Claims::getSubject, secretKey);
        revokeToken(token);
        final String refreshToken = buildToken(username, refreshExpiration, secretKey);
        return new AuthResponse(refreshToken);
    }

    @Override
    public Boolean isTokenValid(String token) {
        if (revokedTokenRepository.findByToken(token).isPresent()) return false;
        try {
            return extractClaim(token, Claims::getExpiration, secretKey).after(new Date());
        } catch (JwtException ex) {
            return false;
        }
    }

    void revokeToken(String jwtToken) {
        RevokedToken token = new RevokedToken();
        token.setToken(jwtToken);
        revokedTokenRepository.save(token);
    }

    String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject, secretKey);
    }

    void changePassword(ChangePasswordRequest request, Principal authenticatedUser) {
        Client client = (Client) ((UsernamePasswordAuthenticationToken) authenticatedUser).getPrincipal();
        if (!passwordEncoder.matches(request.currentPassword(), client.getPassword())) {
            throw new RestException(ExceptionType.WRONG_PASSWORD_EXCEPTION);
        }
        if (!request.newPassword().equals(request.confirmationPassword())) {
            throw new RestException(ExceptionType.PASSWORD_NOT_MATCHING_EXCEPTION);
        }
        client.setPassword(passwordEncoder.encode(request.newPassword()));
        clientRepository.save(client);
        log.info("Password has been successfully changed.");
    }
}
