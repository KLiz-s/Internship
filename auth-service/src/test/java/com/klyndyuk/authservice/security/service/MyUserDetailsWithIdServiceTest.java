package com.klyndyuk.authservice.security.service;

import com.klyndyuk.authservice.entity.Credentials;
import com.klyndyuk.authservice.repository.CredentialsRepository;
import com.klyndyuk.authservice.security.entity.MyUserDetailsWithId;
import com.klyndyuk.authservice.utils.TestConstants;
import com.klyndyuk.authservice.utils.TestCredentials;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsWithIdServiceTest {

    @Mock
    private CredentialsRepository credentialsRepository;

    @InjectMocks
    private MyUserDetailsWithIdService service;

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        Credentials credentials =
                TestCredentials.createCredentials(TestConstants.USER_ID);

        given(credentialsRepository.findByLogin(TestConstants.LOGIN))
                .willReturn(Optional.of(credentials));

        MyUserDetailsWithId result =
                (MyUserDetailsWithId) service.loadUserByUsername(TestConstants.LOGIN);

        assertThat(result.getId())
                .isEqualTo(TestConstants.USER_ID.toString());
        assertThat(result.getUsername())
                .isEqualTo(TestConstants.LOGIN);
        assertThat(result.getPassword())
                .isEqualTo(credentials.getPassword());

        then(credentialsRepository)
                .should()
                .findByLogin(TestConstants.LOGIN);
    }

    @Test
    void loadUserByUsername_shouldThrowWhenCredentialsNotFound() {
        given(credentialsRepository.findByLogin(TestConstants.LOGIN))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.loadUserByUsername(TestConstants.LOGIN))
                .isInstanceOf(UsernameNotFoundException.class);

        then(credentialsRepository)
                .should()
                .findByLogin(TestConstants.LOGIN);
    }

    @Test
    void loadUserById_shouldReturnUserDetails() {
        Credentials credentials =
                TestCredentials.createCredentials(TestConstants.USER_ID);

        given(credentialsRepository.findById(TestConstants.USER_ID))
                .willReturn(Optional.of(credentials));

        MyUserDetailsWithId result =
                (MyUserDetailsWithId) service.loadUserById(
                        TestConstants.USER_ID.toString());

        assertThat(result.getId())
                .isEqualTo(TestConstants.USER_ID.toString());
        assertThat(result.getUsername())
                .isEqualTo(TestConstants.LOGIN);
        assertThat(result.getPassword())
                .isEqualTo(credentials.getPassword());

        then(credentialsRepository)
                .should()
                .findById(TestConstants.USER_ID);
    }

    @Test
    void loadUserById_shouldThrowWhenCredentialsNotFound() {
        given(credentialsRepository.findById(TestConstants.USER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.loadUserById(TestConstants.USER_ID.toString()))
                .isInstanceOf(UsernameNotFoundException.class);

        then(credentialsRepository)
                .should()
                .findById(TestConstants.USER_ID);
    }
}