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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class JwtServiceUtils {
    static <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secretKey) {
        final Claims claims = extractAllClaims(token, secretKey);
        return claimsResolver.apply(claims);
    }

    static private Claims extractAllClaims(String token, String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        SecretKey signInKey = Keys.hmacShaKeyFor(keyBytes);

        return Jwts
                .parser()
                .verifyWith(signInKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    static String buildToken(String username, Long expiration, String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        SecretKey signInKey = Keys.hmacShaKeyFor(keyBytes);
        Map<String, Object> claims = new HashMap<>();

        return Jwts
                .builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signInKey, Jwts.SIG.HS256)
                .compact();
    }

    static String extractBearerToken(final HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "";
        }
        final int bearerTokenPrefixLength = 7;
        return authHeader.substring(bearerTokenPrefixLength);
    }
}
