package com.klyndyuk.orderservice.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    public static final String SECRET = System.getenv("JWT_SECRET");

    private SecretKey key;

    @PostConstruct
    private void getSignKey() {
        if (SECRET == null || SECRET.isBlank()) {
            throw new IllegalStateException(
                    "JWT_SECRET environment variable is not set"
            );
        }

        final byte[] keyBytes;

        try {
            keyBytes = Decoders.BASE64.decode(SECRET);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "JWT_SECRET must be a valid Base64-encoded string",
                    e
            );
        }

        try {
            key = Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "JWT_SECRET must contain at least 256 bits (32 bytes) of key material",
                    e
            );
        }
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUserRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, String id) {
        final String userId = extractUserId(token);
        return (userId.equals(id)) && !isTokenExpired(token);
    }
}
