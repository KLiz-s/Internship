package com.klyndyuk.authservice.utils;

import com.klyndyuk.authservice.entity.Credentials;
import com.klyndyuk.authservice.entity.RefreshToken;

import java.util.Date;

public final class TestRefreshTokens {

    private TestRefreshTokens() {
    }

    public static RefreshToken createRefreshToken() {
        return createRefreshToken(TestCredentials.createCredentials());
    }

    public static RefreshToken createRefreshToken(Credentials credentials) {
        RefreshToken token = new RefreshToken();

        token.setCredentials(credentials);
        token.setTokenHash(TestConstants.REFRESH_TOKEN_HASH);
        token.setCreatedAt(new Date());
        token.setExpiresAt(new Date(System.currentTimeMillis() + TestConstants.TOKEN_LIFETIME));
        token.setRevokedAt(null);
        token.setReplacedBy(null);

        return token;
    }

    public static RefreshToken createExpiredRefreshToken(Credentials credentials) {
        RefreshToken token = createRefreshToken(credentials);
        token.setExpiresAt(new Date(System.currentTimeMillis() - TestConstants.TOKEN_LIFETIME));
        return token;
    }

    public static RefreshToken createRevokedRefreshToken(Credentials credentials) {
        RefreshToken token = createRefreshToken(credentials);
        token.setRevokedAt(new Date());
        return token;
    }

    public static RefreshToken createReplacedRefreshToken(
            Credentials credentials,
            RefreshToken replacement
    ) {
        RefreshToken token = createRevokedRefreshToken(credentials);
        token.setReplacedBy(replacement);
        return token;
    }
}