package com.klyndyuk.authservice.service.impl;

import com.klyndyuk.authservice.dto.response.TwoTokenResponse;
import com.klyndyuk.authservice.entity.Credentials;
import com.klyndyuk.authservice.exception.CredentialsNotFoundException;
import com.klyndyuk.authservice.repository.CredentialsRepository;
import com.klyndyuk.authservice.security.service.JwtService;
import com.klyndyuk.authservice.service.interfaces.RefreshTokenService;
import com.klyndyuk.authservice.service.interfaces.TwoTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TwoTokenServiceImpl implements TwoTokenService {
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CredentialsRepository credentialsRepository;

    @Override
    public TwoTokenResponse generateTokenPair(Credentials credentials) throws NoSuchAlgorithmException {
        String refreshToken = refreshTokenService.generateToken(credentials);
        return new TwoTokenResponse(jwtService.generateToken(refreshTokenService.getUserIdFromToken(refreshToken), credentials.getRole()), refreshToken);
    }

    @Override
    public TwoTokenResponse updateTokenPair(String token) throws NoSuchAlgorithmException {
        String newToken = refreshTokenService.refreshToken(token);
        String id = refreshTokenService.getUserIdFromToken(newToken);
        Credentials credentials = credentialsRepository.findById(UUID.fromString(id))
                .orElseThrow(CredentialsNotFoundException::new);
        return new TwoTokenResponse(jwtService.generateToken(id, credentials.getRole()), newToken);
    }

    @Override
    public void logout(Credentials credentials) {
        refreshTokenService.logout(credentials);
    }
}
