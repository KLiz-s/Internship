package com.klyndyuk.authservice.integration.repository;

import com.klyndyuk.authservice.entity.Credentials;
import com.klyndyuk.authservice.entity.RefreshToken;
import com.klyndyuk.authservice.repository.CredentialsRepository;
import com.klyndyuk.authservice.repository.RefreshTokenRepository;
import com.klyndyuk.authservice.utils.TestConstants;
import com.klyndyuk.authservice.utils.TestCredentials;
import com.klyndyuk.authservice.utils.TestRefreshTokens;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Test
    void findByTokenHash_shouldReturnRefreshToken() {
        Credentials credentials =
                credentialsRepository.save(
                        TestCredentials.createCredentials());

        RefreshToken refreshToken =
                TestRefreshTokens.createRefreshToken(credentials);

        refreshToken = refreshTokenRepository.save(refreshToken);

        entityManager.flush();
        entityManager.clear();

        Optional<RefreshToken> result =
                refreshTokenRepository.findByTokenHash(
                        TestConstants.REFRESH_TOKEN_HASH);

        assertThat(result)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "expiresAt")
                .isEqualTo(refreshToken);

        assertThat(result.get().getCreatedAt().getTime())
                .isEqualTo(refreshToken.getCreatedAt().getTime());

        assertThat(result.get().getExpiresAt().getTime())
                .isEqualTo(refreshToken.getExpiresAt().getTime());
    }

    @Test
    void findByTokenHash_shouldReturnEmptyWhenTokenNotFound() {
        Optional<RefreshToken> result =
                refreshTokenRepository.findByTokenHash(
                        TestConstants.REFRESH_TOKEN_HASH);

        assertThat(result).isEmpty();
    }

    @Test
    void revokeAllActiveByCredentials_shouldRevokeOnlyActiveTokens() {
        Credentials credentials = credentialsRepository.save(
                TestCredentials.createCredentials()
        );

        RefreshToken activeToken1 =
                refreshTokenRepository.save(
                        TestRefreshTokens.createRefreshToken(credentials));

        RefreshToken activeToken2 =
                refreshTokenRepository.save(
                        TestRefreshTokens.createRefreshToken(credentials));

        RefreshToken revokedToken =
                refreshTokenRepository.save(
                        TestRefreshTokens.createRevokedRefreshToken(credentials));

        entityManager.flush();
        entityManager.clear();

        Date revokedAt = revokedToken.getRevokedAt();

        refreshTokenRepository.revokeAllActiveByCredentials(credentials);

        entityManager.flush();
        entityManager.clear();

        RefreshToken persistedActiveToken1 = refreshTokenRepository
                .findById(activeToken1.getId())
                .orElseThrow();

        RefreshToken persistedActiveToken2 = refreshTokenRepository
                .findById(activeToken2.getId())
                .orElseThrow();

        RefreshToken persistedRevokedToken = refreshTokenRepository
                .findById(revokedToken.getId())
                .orElseThrow();

        assertThat(persistedActiveToken1.getRevokedAt())
                .isNotNull();

        assertThat(persistedActiveToken2.getRevokedAt())
                .isNotNull();

        assertThat(persistedRevokedToken.getRevokedAt().getTime())
                .isEqualTo(revokedAt.getTime());
    }
}