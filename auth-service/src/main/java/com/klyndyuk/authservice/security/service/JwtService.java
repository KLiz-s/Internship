package com.klyndyuk.authservice.security.service;

import com.klyndyuk.authservice.enums.Role;
import com.klyndyuk.authservice.security.entity.UserDetailsWithId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    public static final String SECRET = System.getenv("JWT_SECRET");

    private SecretKey key;

    public String generateToken(String userId, Role userRole) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userRole);
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
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        key = Keys.hmacShaKeyFor(keyBytes);
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

    public boolean validateToken(String token, UserDetailsWithId userDetails) {
        final String userId = extractUserId(token);
        return (userId.equals(userDetails.getId())) && !isTokenExpired(token);
    }
}
