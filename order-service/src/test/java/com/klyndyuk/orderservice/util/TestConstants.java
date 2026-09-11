package com.klyndyuk.orderservice.util;

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
    public static final UUID SECOND_ORDER_ID =
            UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
    public static final UUID ITEM_ID =
            UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");
    public static final UUID SECOND_ITEM_ID =
            UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

    public static final String ORDER_STATUS = "CREATED";
    public static final String ITEM_NAME = "Pizza";
    public static final String SECOND_ITEM_NAME = "Soda";
    public static final BigDecimal ITEM_PRICE = new BigDecimal("12.50");
    public static final BigDecimal SECOND_ITEM_PRICE = new BigDecimal("3.00");
    public static final Integer ITEM_QUANTITY = 2;
    public static final Integer SECOND_ITEM_QUANTITY = 1;
    public static final BigDecimal TOTAL_PRICE = new BigDecimal("28.00");
    public static final Instant ORDER_CREATED_AT = Instant.parse("2026-01-01T10:00:00Z");
    public static final Instant ORDER_UPDATED_AT = Instant.parse("2026-01-02T10:00:00Z");

    private TestConstants() {
    }
}
