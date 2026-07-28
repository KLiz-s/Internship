package com.klyndyuk.authservice.security.service;

import com.klyndyuk.authservice.enums.Role;
import com.klyndyuk.authservice.security.entity.UserDetailsWithId;
import com.klyndyuk.authservice.utils.TestConstants;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        String base64Secret =
                "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

        SecretKey key = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(base64Secret)
        );

        ReflectionTestUtils.setField(jwtService, "key", key);
    }

    @Test
    void generateToken_shouldGenerateToken() {
        String token = jwtService.generateToken(
                TestConstants.USER_ID.toString(),
                Role.ROLE_USER
        );

        assertThat(token)
                .isNotBlank();
    }

    @Test
    void extractUserId_shouldReturnUserId() {
        String token = jwtService.generateToken(
                TestConstants.USER_ID.toString(),
                Role.ROLE_USER
        );

        String userId = jwtService.extractUserId(token);

        assertThat(userId)
                .isEqualTo(TestConstants.USER_ID.toString());
    }

    @Test
    void extractUserRole_shouldReturnRole() {
        String token = jwtService.generateToken(
                TestConstants.USER_ID.toString(),
                Role.ROLE_ADMIN
        );

        String role = jwtService.extractUserRole(token);

        assertThat(role)
                .isEqualTo(Role.ROLE_ADMIN.name());
    }

    @Test
    void extractExpiration_shouldReturnFutureDate() {
        long now = System.currentTimeMillis();

        String token = jwtService.generateToken(
                TestConstants.USER_ID.toString(),
                Role.ROLE_USER
        );

        Date expiration = jwtService.extractExpiration(token);

        assertThat(expiration)
                .isAfter(new Date(now))
                .isBefore(new Date(now + 11 * 60 * 1000));
    }

    @Test
    void validateToken_shouldReturnTrue() {
        String token = jwtService.generateToken(
                TestConstants.USER_ID.toString(),
                Role.ROLE_USER
        );

        UserDetailsWithId userDetails = mock(UserDetailsWithId.class);

        given(userDetails.getId())
                .willReturn(TestConstants.USER_ID.toString());

        assertThat(jwtService.validateToken(token, userDetails))
                .isTrue();
    }

    @Test
    void validateToken_shouldReturnFalseForAnotherUser() {
        String token = jwtService.generateToken(
                TestConstants.USER_ID.toString(),
                Role.ROLE_USER
        );

        UserDetailsWithId userDetails = mock(UserDetailsWithId.class);

        given(userDetails.getId())
                .willReturn(UUID.randomUUID().toString());

        assertThat(jwtService.validateToken(token, userDetails))
                .isFalse();
    }
}