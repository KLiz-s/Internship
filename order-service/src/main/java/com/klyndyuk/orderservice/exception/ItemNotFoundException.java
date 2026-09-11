package com.klyndyuk.orderservice.exception;

import java.util.UUID;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(UUID itemId) {
        super("Item with id '%s' not found".formatted(itemId));
    }
}
