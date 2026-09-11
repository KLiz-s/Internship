package com.klyndyuk.authservice.utils;

import java.time.LocalDate;
import java.util.UUID;

public final class TestConstants {
    public static final UUID USER_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    public static final UUID REFRESH_TOKEN_ID =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    public static final String LOGIN = "john";

    public static final String PASSWORD = "password";

    public static final String ENCODED_PASSWORD = "encoded-password";

    public static final String ACCESS_TOKEN = "access-token";

    public static final String REFRESH_TOKEN = "refresh-token";

    public static final String REFRESH_TOKEN_HASH = "refresh-token-hash";

    public static final long TOKEN_LIFETIME = 60_000L;

    public static final String USER_NAME = "John";
    public static final String USER_SURNAME = "Smith";
    public static final String USER_EMAIL = "john.smith@test.com";
    public static final LocalDate USER_BIRTH_DATE =
            LocalDate.of(2000, 1, 1);


    private TestConstants() {
    }
}