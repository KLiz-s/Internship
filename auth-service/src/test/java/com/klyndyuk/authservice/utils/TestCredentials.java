package com.klyndyuk.authservice.utils;

import com.klyndyuk.authservice.entity.Credentials;
import com.klyndyuk.authservice.enums.Role;

import java.util.UUID;

public final class TestCredentials {
    private TestCredentials() {
    }

    public static Credentials createCredentials() {
        Credentials credentials = new Credentials();

        credentials.setLogin(TestConstants.LOGIN);
        credentials.setPassword(TestConstants.ENCODED_PASSWORD);
        credentials.setRole(Role.ROLE_USER);

        return credentials;
    }

    public static Credentials createCredentials(UUID id) {
        Credentials credentials = createCredentials();
        credentials.setUserId(id);
        return credentials;
    }

    public static Credentials createCredentials(String login) {
        Credentials credentials = createCredentials();
        credentials.setLogin(login);
        return credentials;
    }


    public static Credentials createAdmin() {
        Credentials credentials = createCredentials();
        credentials.setRole(Role.ROLE_ADMIN);
        return credentials;
    }
}