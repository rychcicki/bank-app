package com.example.bank.auth;

import com.example.bank.registration.ClientRequest;
import com.example.bank.registration.jpa.Client;
import com.example.bank.registration.jpa.ClientRepository;
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

import static com.example.bank.registration.ClientService.clientLessThan18YearsOldMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final ClientRepository clientRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    @Value("${age-of-majority}")
    @Getter
    @Setter(AccessLevel.PACKAGE) // only for testing
    private Integer majority;

    public AuthenticationResponse register(/*RegisterRequest */ ClientRequest clientRequest) {
        log.info("Start client's registration.");
        int years = Period.between(clientRequest.birthDate(), LocalDate.now())
                .getYears();
        if (years >= getMajority()) {
            Client client = new Client(
                    clientRequest.firstName(),
                    clientRequest.lastName(),
                    clientRequest.birthDate(),
                    clientRequest.email(),
                    clientRequest.password(),
                    clientRequest.address());
            clientRepository.save(client);
            log.info("Client's registration passed.");
//            return client;

            Client savedClient = clientRepository.save(client);
            String jwtToken = jwtService.generateToken(client);
            String refreshToken = jwtService.generateRefreshToken(client);
            saveUserToken(savedClient, jwtToken);

            return AuthenticationResponse
                    .builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        } else {
            log.error("Client's registration failed.");
            throw new IllegalArgumentException(clientLessThan18YearsOldMessage);
        }

//        User user = User
//                .builder()
//                .firstName(request.getFirstName())
//                .lastName(request.getLastName())
//                .email(request.getEmail())
//                .role(Role.ADMIN)
//                .password(passwordEncoder.encode(request.getPassword()))
//                .build();
    }

    private void saveUserToken(Client client, String jwtToken) {
        Token token = Token
                .builder()
                .client(client)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .tokenType(TokenType.BEARER)
                .build();
        tokenRepository.save(token);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken;
        String userEmail;
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            Client client = this.clientRepository.findByEmail(userEmail).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, client)) {
                String accessToken = jwtService.generateToken(client);
                revokeAllClientToken(client);
                saveUserToken(client, accessToken);
                AuthenticationResponse authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    private void revokeAllClientToken(Client client) {
        List<Token> allValidTokenByUser = tokenRepository.findAllValidTokenByClient(Math.toIntExact(client.getId()));
        if (allValidTokenByUser.isEmpty()) return;
        allValidTokenByUser.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(allValidTokenByUser);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
                authenticationRequest.getPassword()));
        Client client = clientRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow();
        String token = jwtService.generateToken(client);
        String refreshToken = jwtService.generateRefreshToken(client);
        revokeAllClientToken(client);
        saveUserToken(client, token);
        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
    }
}
