package com.klyndyuk.authservice.service;

import com.klyndyuk.authservice.service.impl.RefreshTokenHasherImpl;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenHasherImplTest {

    private final RefreshTokenHasherImpl hasher =
            new RefreshTokenHasherImpl();

    @Test
    void hash_shouldReturnSameHashForSameToken()
            throws NoSuchAlgorithmException {

        String hash1 = hasher.hash("token");
        String hash2 = hasher.hash("token");

        assertThat(hash1)
                .isEqualTo(hash2);
    }

    @Test
    void hash_shouldReturnDifferentHashesForDifferentTokens()
            throws NoSuchAlgorithmException {

        String hash1 = hasher.hash("token-1");
        String hash2 = hasher.hash("token-2");

        assertThat(hash1)
                .isNotEqualTo(hash2);
    }
}