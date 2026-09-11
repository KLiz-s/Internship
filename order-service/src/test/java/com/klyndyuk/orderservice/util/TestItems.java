package com.klyndyuk.orderservice.util;

import com.klyndyuk.orderservice.entity.Item;

import java.time.Instant;
import java.util.UUID;

public final class TestItems {
    private TestItems() {
    }

    public static Item createItem() {
        return createItem(
                TestConstants.ITEM_ID,
                TestConstants.ITEM_NAME,
                TestConstants.ITEM_PRICE
        );
    }

    public static Item createSecondItem() {
        return createItem(
                TestConstants.SECOND_ITEM_ID,
                TestConstants.SECOND_ITEM_NAME,
                TestConstants.SECOND_ITEM_PRICE
        );
    }

    public static Item createItemForPersist() {
        Item item = createItem(
                null,
                TestConstants.ITEM_NAME,
                TestConstants.ITEM_PRICE
        );
        item.setUpdatedAt(Instant.now());
        return item;
    }

    public static Item createSecondItemForPersist() {
        Item item = createItem(
                null,
                TestConstants.SECOND_ITEM_NAME,
                TestConstants.SECOND_ITEM_PRICE
        );
        item.setUpdatedAt(Instant.now());
        return item;
    }

    public static Item createItem(UUID id, String name, java.math.BigDecimal price) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setPrice(price);
        item.setCreatedAt(Instant.now());
        item.setUpdatedAt(Instant.now());
        return item;
    }
}
