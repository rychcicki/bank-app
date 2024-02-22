package com.example.bank.security.auth;

import com.example.bank.client.ClientRequest;
import com.example.bank.client.jpa.Client;
import com.example.bank.client.jpa.ClientRepository;
import com.example.bank.security.config.JwtService;
import com.example.bank.security.token.Token;
import com.example.bank.security.token.TokenRepository;
import com.example.bank.security.token.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static com.example.bank.client.ClientService.CLIENT_LESS_THAN_18_YEARS_OLD_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final ClientRepository clientRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Value("${age-of-majority}")
    @Getter
    @Setter(AccessLevel.PACKAGE) // only for testing
    private Integer majority;

    public AuthenticationResponse registerClient(ClientRequest clientRequest) {
        log.info("Start client's registration.");
        int years = Period.between(clientRequest.birthDate(), LocalDate.now()).getYears();
        if (years >= getMajority()) {
            Client client = new Client(
                    clientRequest.firstName(),
                    clientRequest.lastName(),
                    clientRequest.birthDate(),
                    clientRequest.email(),
                    passwordEncoder.encode((clientRequest.password())),
                    clientRequest.address());
            Client savedClient = clientRepository.save(client);
            log.info("Client's registration passed.");

            String jwtToken = jwtService.generateToken(client);
            String refreshToken = jwtService.generateRefreshToken(client);
            saveClientToken(savedClient, jwtToken);
            return new AuthenticationResponse(jwtToken, refreshToken);
        } else {
            log.error("Client's registration failed.");
            throw new IllegalArgumentException(CLIENT_LESS_THAN_18_YEARS_OLD_MESSAGE);
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),
                request.password()));
        Client client = clientRepository.findByEmail(request.email()).orElseThrow();
        String jwtToken = jwtService.generateToken(client);
        String refreshToken = jwtService.generateRefreshToken(client);
        revokeAllClientTokens(client);
        saveClientToken(client, jwtToken);
        return new AuthenticationResponse(jwtToken, refreshToken);
    }

    private void saveClientToken(Client client, String jwtToken) {
        Token token = Token.builder()
                .client(client)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllClientTokens(Client client) {
        List<Token> validClientTokens = tokenRepository.findAllValidTokenByClientId(client.getId());
        if (validClientTokens.isEmpty()) return;
        validClientTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validClientTokens);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String clientEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        clientEmail = jwtService.extractUsername(refreshToken);
        if (clientEmail != null) {
            Client client = this.clientRepository.findByEmail(clientEmail).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, client)) {
                String accessToken = jwtService.generateToken(client);
                revokeAllClientTokens(client);
                saveClientToken(client, accessToken);
                AuthenticationResponse authResponse = new AuthenticationResponse(accessToken, refreshToken);
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
