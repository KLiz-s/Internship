package com.klyndyuk.authservice.service;

import com.klyndyuk.authservice.entity.Credentials;
import com.klyndyuk.authservice.entity.RefreshToken;
import com.klyndyuk.authservice.exception.TokenNotFoundException;
import com.klyndyuk.authservice.exception.TokenRevokedOrExpiredException;
import com.klyndyuk.authservice.repository.RefreshTokenRepository;
import com.klyndyuk.authservice.service.impl.RefreshTokenServiceImpl;
import com.klyndyuk.authservice.service.interfaces.RefreshTokenHasher;
import com.klyndyuk.authservice.utils.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RefreshTokenHasher refreshTokenHasher;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Captor
    private ArgumentCaptor<RefreshToken> refreshTokenCaptor;

    @Test
    void generateToken_shouldSaveRefreshToken() throws Exception {
        Credentials credentials = TestCredentials.createCredentials();

        given(refreshTokenHasher.hash(anyString()))
                .willReturn(TestConstants.REFRESH_TOKEN_HASH);

        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        String token = refreshTokenService.generateToken(credentials);

        assertThat(token).isNotBlank();

        then(refreshTokenRepository)
                .should()
                .save(refreshTokenCaptor.capture());

        RefreshToken saved = refreshTokenCaptor.getValue();

        assertThat(saved.getCredentials()).isEqualTo(credentials);
        assertThat(saved.getTokenHash())
                .isEqualTo(TestConstants.REFRESH_TOKEN_HASH);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getExpiresAt()).isAfter(saved.getCreatedAt());
        assertThat(saved.getRevokedAt()).isNull();
        assertThat(saved.getReplacedBy()).isNull();
    }

    @Test
    void refreshToken_shouldGenerateReplacementToken() throws Exception {
        Credentials credentials = TestCredentials.createCredentials();

        RefreshToken current =
                TestRefreshTokens.createRefreshToken(credentials);

        given(refreshTokenHasher.hash(TestConstants.REFRESH_TOKEN))
                .willReturn(TestConstants.REFRESH_TOKEN_HASH);

        given(refreshTokenRepository.findByTokenHash(
                TestConstants.REFRESH_TOKEN_HASH))
                .willReturn(Optional.of(current));

        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        String token =
                refreshTokenService.refreshToken(TestConstants.REFRESH_TOKEN);

        assertThat(token).isNotBlank();

        verify(refreshTokenRepository, times(2))
                .save(refreshTokenCaptor.capture());

        List<RefreshToken> saved = refreshTokenCaptor.getAllValues();

        assertThat(saved).hasSize(2);

        RefreshToken newToken = saved.get(0);
        RefreshToken oldToken = saved.get(1);

        assertThat(newToken.getCredentials())
                .isEqualTo(credentials);

        assertThat(oldToken.getRevokedAt())
                .isNotNull();

        assertThat(oldToken.getReplacedBy())
                .isEqualTo(newToken);
    }

    @Test
    void getUserIdFromToken_shouldReturnUserId() throws Exception {
        Credentials credentials =
                TestCredentials.createCredentials(TestConstants.USER_ID);

        RefreshToken token =
                TestRefreshTokens.createRefreshToken(credentials);

        given(refreshTokenHasher.hash(TestConstants.REFRESH_TOKEN))
                .willReturn(TestConstants.REFRESH_TOKEN_HASH);

        given(refreshTokenRepository.findByTokenHash(
                TestConstants.REFRESH_TOKEN_HASH))
                .willReturn(Optional.of(token));

        String result =
                refreshTokenService.getUserIdFromToken(
                        TestConstants.REFRESH_TOKEN);

        assertThat(result)
                .isEqualTo(TestConstants.USER_ID.toString());
    }

    @Test
    void getUserIdFromToken_shouldThrowWhenTokenNotFound()
            throws Exception {

        given(refreshTokenHasher.hash(TestConstants.REFRESH_TOKEN))
                .willReturn(TestConstants.REFRESH_TOKEN_HASH);

        given(refreshTokenRepository.findByTokenHash(
                TestConstants.REFRESH_TOKEN_HASH))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                refreshTokenService.getUserIdFromToken(
                        TestConstants.REFRESH_TOKEN))
                .isInstanceOf(TokenNotFoundException.class);
    }

    @Test
    void getUserIdFromToken_shouldThrowWhenTokenRevoked()
            throws Exception {

        Credentials credentials =
                TestCredentials.createCredentials();

        RefreshToken token =
                TestRefreshTokens.createRevokedRefreshToken(credentials);

        given(refreshTokenHasher.hash(TestConstants.REFRESH_TOKEN))
                .willReturn(TestConstants.REFRESH_TOKEN_HASH);

        given(refreshTokenRepository.findByTokenHash(
                TestConstants.REFRESH_TOKEN_HASH))
                .willReturn(Optional.of(token));

        assertThatThrownBy(() ->
                refreshTokenService.getUserIdFromToken(
                        TestConstants.REFRESH_TOKEN))
                .isInstanceOf(TokenRevokedOrExpiredException.class);

        then(refreshTokenRepository)
                .should()
                .revokeAllActiveByCredentials(credentials);
    }

    @Test
    void getUserIdFromToken_shouldThrowWhenTokenExpired()
            throws Exception {

        Credentials credentials =
                TestCredentials.createCredentials();

        RefreshToken token =
                TestRefreshTokens.createExpiredRefreshToken(credentials);

        given(refreshTokenHasher.hash(TestConstants.REFRESH_TOKEN))
                .willReturn(TestConstants.REFRESH_TOKEN_HASH);

        given(refreshTokenRepository.findByTokenHash(
                TestConstants.REFRESH_TOKEN_HASH))
                .willReturn(Optional.of(token));

        assertThatThrownBy(() ->
                refreshTokenService.getUserIdFromToken(
                        TestConstants.REFRESH_TOKEN))
                .isInstanceOf(TokenRevokedOrExpiredException.class);

        then(refreshTokenRepository)
                .should(never())
                .revokeAllActiveByCredentials(any());
    }

    @Test
    void logout_shouldRevokeAllActiveTokens() {
        Credentials credentials =
                TestCredentials.createCredentials();

        refreshTokenService.logout(credentials);

        then(refreshTokenRepository)
                .should()
                .revokeAllActiveByCredentials(credentials);
    }
}
