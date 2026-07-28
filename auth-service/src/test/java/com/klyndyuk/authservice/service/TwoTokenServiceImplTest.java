package com.klyndyuk.authservice.service;

import com.klyndyuk.authservice.dto.response.TwoTokenResponse;
import com.klyndyuk.authservice.entity.Credentials;
import com.klyndyuk.authservice.exception.CredentialsNotFoundException;
import com.klyndyuk.authservice.repository.CredentialsRepository;
import com.klyndyuk.authservice.security.service.JwtService;
import com.klyndyuk.authservice.service.impl.TwoTokenServiceImpl;
import com.klyndyuk.authservice.service.interfaces.RefreshTokenService;
import com.klyndyuk.authservice.utils.TestConstants;
import com.klyndyuk.authservice.utils.TestCredentials;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class TwoTokenServiceImplTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private CredentialsRepository credentialsRepository;

    @InjectMocks
    private TwoTokenServiceImpl twoTokenService;

    @Test
    void generateTokenPair_shouldReturnTokenPair()
            throws NoSuchAlgorithmException {

        Credentials credentials = TestCredentials.createCredentials();

        given(refreshTokenService.generateToken(credentials))
                .willReturn(TestConstants.REFRESH_TOKEN);

        given(refreshTokenService.getUserIdFromToken(TestConstants.REFRESH_TOKEN))
                .willReturn(TestConstants.USER_ID.toString());

        given(jwtService.generateToken(
                TestConstants.USER_ID.toString(),
                credentials.getRole()))
                .willReturn(TestConstants.ACCESS_TOKEN);

        TwoTokenResponse result =
                twoTokenService.generateTokenPair(credentials);

        assertThat(result.getAccessToken())
                .isEqualTo(TestConstants.ACCESS_TOKEN);

        assertThat(result.getRefreshToken())
                .isEqualTo(TestConstants.REFRESH_TOKEN);

        then(refreshTokenService)
                .should()
                .generateToken(credentials);

        then(jwtService)
                .should()
                .generateToken(
                        TestConstants.USER_ID.toString(),
                        credentials.getRole());
    }

    @Test
    void updateTokenPair_shouldReturnNewTokenPair()
            throws NoSuchAlgorithmException {

        Credentials credentials = TestCredentials.createCredentials();

        given(refreshTokenService.refreshToken(TestConstants.REFRESH_TOKEN))
                .willReturn("new-refresh");

        given(refreshTokenService.getUserIdFromToken("new-refresh"))
                .willReturn(TestConstants.USER_ID.toString());

        given(credentialsRepository.findById(TestConstants.USER_ID))
                .willReturn(Optional.of(credentials));

        given(jwtService.generateToken(
                TestConstants.USER_ID.toString(),
                credentials.getRole()))
                .willReturn(TestConstants.ACCESS_TOKEN);

        TwoTokenResponse result =
                twoTokenService.updateTokenPair(TestConstants.REFRESH_TOKEN);

        assertThat(result.getAccessToken())
                .isEqualTo(TestConstants.ACCESS_TOKEN);

        assertThat(result.getRefreshToken())
                .isEqualTo("new-refresh");

        then(refreshTokenService)
                .should()
                .refreshToken(TestConstants.REFRESH_TOKEN);

        then(credentialsRepository)
                .should()
                .findById(TestConstants.USER_ID);
    }

    @Test
    void updateTokenPair_shouldThrowWhenCredentialsNotFound()
            throws NoSuchAlgorithmException {

        given(refreshTokenService.refreshToken(TestConstants.REFRESH_TOKEN))
                .willReturn("new-refresh");

        given(refreshTokenService.getUserIdFromToken("new-refresh"))
                .willReturn(TestConstants.USER_ID.toString());

        given(credentialsRepository.findById(TestConstants.USER_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                twoTokenService.updateTokenPair(TestConstants.REFRESH_TOKEN))
                .isInstanceOf(CredentialsNotFoundException.class);
    }

    @Test
    void logout_shouldDelegateToRefreshTokenService() {
        Credentials credentials = TestCredentials.createCredentials();

        twoTokenService.logout(credentials);

        then(refreshTokenService)
                .should()
                .logout(credentials);
    }
}