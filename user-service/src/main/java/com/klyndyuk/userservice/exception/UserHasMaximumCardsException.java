package com.klyndyuk.userservice.exception;

import java.util.UUID;

public class UserHasMaximumCardsException extends RuntimeException {

    public UserHasMaximumCardsException(UUID userId) {
        super("User with id '%s' already has maximum number of payment cards".formatted(userId));
    }
}