package com.example.bank.security;

import com.example.bank.client.ClientDTO;
import com.example.bank.client.ClientRepository;
import com.example.bank.client.ClientRequest;
import com.example.bank.client.ClientService;
import com.example.bank.client.model.Client;
import com.example.bank.client.model.Status;
import com.example.bank.exception.ExceptionType;
import com.example.bank.exception.RestException;
import com.example.bank.security.model.RevokedToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Value("${age-of-majority:21}")
    @Getter
    @Setter(AccessLevel.PACKAGE) // only for testing
    private Integer majority;

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

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
    public AuthResponse refreshToken(final String authHeader) {
        String token = extractBearerToken(authHeader)
                .filter(this::isTokenValid)
                .orElseThrow(() -> new RestException(ExceptionType.INVALID_JWT_EXCEPTION));

        final String username = extractClaim(token, Claims::getSubject, secretKey);
        revokeToken(token);
        final String newRefreshToken = buildToken(username, refreshExpiration, secretKey);
        return new AuthResponse(newRefreshToken);
    }

    @Override
    public boolean isTokenValid(String token) {
        if (revokedTokenRepository.findByToken(token).isPresent()) return false;
        try {
            return extractClaim(token, Claims::getExpiration, secretKey).after(new Date());
        } catch (JwtException ex) {
            return false;
        }
    }

    void revokeToken(String token) {
        RevokedToken revokedToken = new RevokedToken();
        revokedToken.setToken(token);
        try {
            revokedTokenRepository.save(revokedToken);
        } catch (DataIntegrityViolationException ex) {
            String preview = JwtServiceUtils.previewToken(token);
            log.warn("Token already revoked, preview: {}", preview);
        }
    }

    String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject, secretKey);
    }

    @Transactional
    void changePassword(ChangePasswordRequest request, Principal authenticatedUser) {
        String email = ((UserDetails) ((UsernamePasswordAuthenticationToken) authenticatedUser).getPrincipal())
                .getUsername();
        Client client = clientRepository.findByStatusAndEmail(Status.ACTIVE, email)
                .orElseThrow(() -> new RestException(ExceptionType.CLIENT_NOT_FOUND_EXCEPTION));

        if (!passwordEncoder.matches(request.oldPassword(), client.getPassword())) {
            throw new RestException(ExceptionType.WRONG_PASSWORD_EXCEPTION);
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new RestException(ExceptionType.PASSWORD_NOT_MATCHING_EXCEPTION);
        }
        client.setPassword(passwordEncoder.encode(request.newPassword()));
        log.info("Password has been successfully changed.");
    }
}
