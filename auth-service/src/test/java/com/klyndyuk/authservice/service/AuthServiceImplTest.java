package com.klyndyuk.authservice.service;

import com.klyndyuk.authservice.dto.request.LoginRequest;
import com.klyndyuk.authservice.dto.request.RefreshRequest;
import com.klyndyuk.authservice.dto.request.RegistrationRequest;
import com.klyndyuk.authservice.dto.response.TwoTokenResponse;
import com.klyndyuk.authservice.entity.Credentials;
import com.klyndyuk.authservice.exception.CredentialsNotFoundException;
import com.klyndyuk.authservice.exception.InvalidTokenException;
import com.klyndyuk.authservice.exception.LoginOccupiedException;
import com.klyndyuk.authservice.mapper.CredentialsMapper;
import com.klyndyuk.authservice.repository.CredentialsRepository;
import com.klyndyuk.authservice.security.entity.MyUserDetailsWithId;
import com.klyndyuk.authservice.security.entity.UserDetailsWithId;
import com.klyndyuk.authservice.security.service.JwtService;
import com.klyndyuk.authservice.security.service.UserDetailsWithIdService;
import com.klyndyuk.authservice.service.impl.AuthServiceImpl;
import com.klyndyuk.authservice.service.interfaces.TwoTokenService;
import com.klyndyuk.authservice.utils.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private CredentialsRepository credentialsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CredentialsMapper credentialsMapper;

    @Mock
    private TwoTokenService twoTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsWithIdService userDetailsService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldRegisterCredentials() {
        RegistrationRequest request = TestRegistrationRequests.createRegistrationRequest();
        Credentials credentials = TestCredentials.createCredentials();

        when(credentialsRepository.existsByLogin(request.getLogin())).thenReturn(false);
        when(credentialsMapper.fromRegistrationRequest(request)).thenReturn(credentials);
        when(passwordEncoder.encode(request.getPassword())).thenReturn(TestConstants.ENCODED_PASSWORD);

        authService.register(request);

        ArgumentCaptor<Credentials> captor = ArgumentCaptor.forClass(Credentials.class);

        verify(credentialsRepository).save(captor.capture());

        Credentials saved = captor.getValue();

        assertThat(saved.getLogin()).isEqualTo(TestConstants.LOGIN);
        assertThat(saved.getPassword()).isEqualTo(TestConstants.ENCODED_PASSWORD);
        assertThat(saved.getRole()).isEqualTo(TestCredentials.createCredentials().getRole());
    }

    @Test
    void shouldThrowWhenLoginAlreadyExists() {
        RegistrationRequest request = TestRegistrationRequests.createRegistrationRequest();

        when(credentialsRepository.existsByLogin(request.getLogin())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(LoginOccupiedException.class);

        verify(credentialsRepository, never()).save(any());
    }

    @Test
    void shouldLogin() throws NoSuchAlgorithmException {
        LoginRequest request = TestLoginRequests.createLoginRequest();
        Credentials credentials = TestCredentials.createCredentials();
        TwoTokenResponse response = TestResponses.createTwoTokenResponse();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(credentialsRepository.findByLogin(request.getLogin()))
                .thenReturn(Optional.of(credentials));
        when(twoTokenService.generateTokenPair(credentials))
                .thenReturn(response);

        assertThat(authService.login(request)).isEqualTo(response);

        verify(twoTokenService).generateTokenPair(credentials);
    }

    @Test
    void shouldThrowWhenAuthenticationFailed() {
        LoginRequest request = TestLoginRequests.createLoginRequest();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UsernameNotFoundException.class);

        verifyNoInteractions(twoTokenService);
    }

    @Test
    void shouldThrowWhenCredentialsNotFoundDuringLogin() {
        LoginRequest request = TestLoginRequests.createLoginRequest();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(credentialsRepository.findByLogin(request.getLogin()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CredentialsNotFoundException.class);
    }

    @Test
    void shouldRefreshTokenPair() throws NoSuchAlgorithmException {
        RefreshRequest request = TestRefreshRequests.createRefreshRequest();

        TwoTokenResponse response = TestResponses.createTwoTokenResponse();

        when(twoTokenService.updateTokenPair(request.getToken()))
                .thenReturn(response);

        assertThat(authService.refresh(request)).isEqualTo(response);

        verify(twoTokenService).updateTokenPair(request.getToken());
    }

    @Test
    void shouldLogout() {
        Credentials credentials = TestCredentials.createCredentials();

        MyUserDetailsWithId principal = mock(MyUserDetailsWithId.class);

        when(principal.getId()).thenReturn(TestConstants.USER_ID.toString());
        when(credentialsRepository.findById(TestConstants.USER_ID))
                .thenReturn(Optional.of(credentials));

        authService.logout(principal);

        verify(twoTokenService).logout(credentials);
    }

    @Test
    void shouldThrowWhenCredentialsNotFoundDuringLogout() {
        MyUserDetailsWithId principal = mock(MyUserDetailsWithId.class);

        UUID id = UUID.randomUUID();

        when(principal.getId()).thenReturn(id.toString());
        when(credentialsRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.logout(principal))
                .isInstanceOf(CredentialsNotFoundException.class);

        verifyNoInteractions(twoTokenService);
    }

    @Test
    void shouldValidateToken() {
        String token = "token";
        String userId = UUID.randomUUID().toString();

        UserDetailsWithId userDetails = mock(UserDetailsWithId.class);

        when(jwtService.extractUserId(token))
                .thenReturn(userId);

        when(userDetailsService.loadUserById(userId))
                .thenReturn(userDetails);

        when(jwtService.validateToken(token, userDetails))
                .thenReturn(true);

        authService.validateToken(token);

        verify(jwtService).extractUserId(token);
        verify(userDetailsService).loadUserById(userId);
        verify(jwtService).validateToken(token, userDetails);
    }

    @Test
    void shouldThrowWhenTokenIsInvalid() {
        String token = "token";
        String userId = UUID.randomUUID().toString();

        UserDetailsWithId userDetails = mock(UserDetailsWithId.class);

        when(jwtService.extractUserId(token))
                .thenReturn(userId);

        when(userDetailsService.loadUserById(userId))
                .thenReturn(userDetails);

        when(jwtService.validateToken(token, userDetails))
                .thenReturn(false);

        assertThrows(
                InvalidTokenException.class,
                () -> authService.validateToken(token)
        );
    }
}