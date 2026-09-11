package com.klyndyuk.paymentservice.util;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class TestConstants {

    public static final UUID USER_ID =
            UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    public static final UUID SECOND_USER_ID =
            UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    public static final UUID ORDER_ID =
            UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

    public static final UUID PAYMENT_ID =
            UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

    public static final BigDecimal PAYMENT_AMOUNT =
            new BigDecimal("28.00");

    public static final String PAYMENT_STATUS_SUCCESS = "SUCCESS";
    public static final String PAYMENT_STATUS_FAILED = "FAILED";

    public static final Instant PAYMENT_TIMESTAMP =
            Instant.parse("2026-01-01T10:00:00Z");

    private TestConstants() {
    }
}