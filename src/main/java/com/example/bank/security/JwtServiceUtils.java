package com.example.bank.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class JwtServiceUtils {
    static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";
    private static final int PREVIEW_LENGTH = 15;

    static <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secretKey) {
        final Claims claims = extractAllClaims(token, secretKey);
        return claimsResolver.apply(claims);
    }

    static private Claims extractAllClaims(String token, String secretKey) {
        return Jwts.parser()
                .verifyWith(decodeSigningKey(secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    static String buildToken(String username, Long expiration, String secretKey) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(decodeSigningKey(secretKey))
                .compact();
    }

    private static SecretKey decodeSigningKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    static Optional<String> extractBearerToken(final HttpServletRequest request) {
        return extractBearerToken(request.getHeader(HttpHeaders.AUTHORIZATION));
    }

    static Optional<String> extractBearerToken(final String authHeader) {
        return Optional.ofNullable(authHeader)
                .filter(token -> token.startsWith(AUTHORIZATION_HEADER_PREFIX))
                .map(token -> token.substring(AUTHORIZATION_HEADER_PREFIX.length()));
    }

    static String previewToken(String token) {
        return token.length() > PREVIEW_LENGTH ? token.substring(0, PREVIEW_LENGTH) + "..." : token;
    }
}
