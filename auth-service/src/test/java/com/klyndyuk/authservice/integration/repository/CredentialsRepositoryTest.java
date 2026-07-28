package com.klyndyuk.authservice.integration.repository;

import com.klyndyuk.authservice.entity.Credentials;
import com.klyndyuk.authservice.repository.CredentialsRepository;
import com.klyndyuk.authservice.utils.TestConstants;
import com.klyndyuk.authservice.utils.TestCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CredentialsRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Test
    void findByLogin_shouldReturnCredentials() {
        Credentials credentials = TestCredentials.createCredentials();

        credentials = credentialsRepository.save(credentials);
        entityManager.flush();
        entityManager.clear();

        Optional<Credentials> result =
                credentialsRepository.findByLogin(TestConstants.LOGIN);

        assertThat(result)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(credentials);
    }

    @Test
    void findByLogin_shouldReturnEmptyWhenCredentialsNotFound() {
        Optional<Credentials> result =
                credentialsRepository.findByLogin(TestConstants.LOGIN);

        assertThat(result).isEmpty();
    }

    @Test
    void existsByLogin_shouldReturnTrueWhenCredentialsExist() {
        Credentials credentials = TestCredentials.createCredentials();

        credentialsRepository.save(credentials);

        boolean exists =
                credentialsRepository.existsByLogin(TestConstants.LOGIN);

        assertThat(exists).isTrue();
    }

    @Test
    void existsByLogin_shouldReturnFalseWhenCredentialsDoNotExist() {
        boolean exists =
                credentialsRepository.existsByLogin(TestConstants.LOGIN);

        assertThat(exists).isFalse();
    }
}