package com.klyndyuk.authservice.service.impl;

import com.klyndyuk.authservice.entity.Credentials;
import com.klyndyuk.authservice.entity.RefreshToken;
import com.klyndyuk.authservice.exception.TokenNotFoundException;
import com.klyndyuk.authservice.exception.TokenRevokedOrExpiredException;
import com.klyndyuk.authservice.repository.RefreshTokenRepository;
import com.klyndyuk.authservice.service.interfaces.RefreshTokenHasher;
import com.klyndyuk.authservice.service.interfaces.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenHasher refreshTokenHasher;

    @Override
    public String generateToken(Credentials credentials) throws NoSuchAlgorithmException {
        return generateToken(credentials, null);
    }

    private String generateToken(Credentials credentials, RefreshToken previous) throws NoSuchAlgorithmException {
        RefreshToken token = new RefreshToken();
        token.setCreatedAt(new Date());
        token.setExpiresAt(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30));
        token.setCredentials(credentials);

        SecureRandom secureRandom = new SecureRandom();

        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        String tokenString = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        String tokenHash = refreshTokenHasher.hash(tokenString);

        token.setTokenHash(tokenHash);
        token = refreshTokenRepository.save(token);

        if (previous != null) {
            previous.setReplacedBy(token);
            previous.setRevokedAt(new Date());
            refreshTokenRepository.save(previous);
        }
        return tokenString;
    }

    @Override
    public String refreshToken(String refreshToken) throws NoSuchAlgorithmException {
        RefreshToken current = getRefreshToken(refreshToken);
        Credentials user = current.getCredentials();
        return generateToken(user, current);
    }

    @Override
    public String getUserIdFromToken(String token) throws NoSuchAlgorithmException {
        return getRefreshToken(token).getCredentials().getUserId().toString();
    }

    private RefreshToken getRefreshToken(String token) throws NoSuchAlgorithmException {
        String tokenHash = refreshTokenHasher.hash(token);

        RefreshToken current = refreshTokenRepository.findByTokenHash(tokenHash).orElseThrow(TokenNotFoundException::new);
        if (current.getRevokedAt() != null) {
            refreshTokenRepository.revokeAllActiveByCredentials(current.getCredentials());
            throw new TokenRevokedOrExpiredException();
        }

        if (current.getExpiresAt().before(new Date())) {
            throw new TokenRevokedOrExpiredException();
        }

        return current;
    }

    @Override
    public void logout(Credentials credentials) {
        refreshTokenRepository.revokeAllActiveByCredentials(credentials);
    }
}

