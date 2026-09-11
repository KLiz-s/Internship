package com.klyndyuk.paymentservice.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class TestJwtGenerator {

    public static final String SECRET = System.getenv("JWT_SECRET");

    private SecretKey key;

    public TestJwtGenerator() {
        getSignKey();
    }

    public String generateToken(String userId, TestRole userRole) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userRole.toString());
        return createToken(claims, userId);
    }

    private String createToken(Map<String, Object> claims, String userId) {
        return Jwts.builder().claims(claims).subject(userId).issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(key)
                .compact();
    }

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
}
