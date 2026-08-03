package com.klyndyuk.userservice.util;

import java.time.LocalDate;
import java.util.UUID;

public final class TestConstants {

    private TestConstants() {
    }

    // Users

    public static final UUID USER_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    public static final UUID USER_ID_2 =
            UUID.fromString("11111111-1111-1111-1111-111111111112");

    public static final String USER_NAME = "John";
    public static final String USER_NAME_2 = "Jane";

    public static final String USER_SURNAME = "Smith";
    public static final String USER_SURNAME_2 = "Doe";

    public static final String USER_EMAIL = "john.smith@test.com";
    public static final String USER_EMAIL_2 = "jane.doe@test.com";

    public static final LocalDate USER_BIRTH_DATE =
            LocalDate.of(2000, 1, 1);

    public static final LocalDate USER_BIRTH_DATE_2 =
            LocalDate.of(1998, 5, 15);

    // Payment cards

    public static final UUID PAYMENT_CARD_ID =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    public static final UUID PAYMENT_CARD_ID_2 =
            UUID.fromString("22222222-2222-2222-2222-222222222223");

    public static final String CARD_NUMBER =
            "1111222233334444";

    public static final String CARD_NUMBER_2 =
            "5555666677778888";

    public static final String CARD_HOLDER =
            "JOHN SMITH";

    public static final String CARD_HOLDER_2 =
            "JANE DOE";

    public static final LocalDate CARD_EXPIRATION_DATE =
            LocalDate.of(2030, 12, 31);

    public static final LocalDate CARD_EXPIRATION_DATE_2 =
            LocalDate.of(2031, 6, 30);

    // Updated values

    public static final String UPDATED_USER_NAME = "Updated";
    public static final String UPDATED_USER_SURNAME = "User";
    public static final String UPDATED_USER_EMAIL = "updated@test.com";

    public static final String UPDATED_CARD_HOLDER =
            "UPDATED HOLDER";
}