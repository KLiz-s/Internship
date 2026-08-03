package com.klyndyuk.userservice.exception;

import java.util.UUID;

public class PaymentCardNotFoundException extends RuntimeException {

    public PaymentCardNotFoundException(UUID id) {
        super("Payment card with id '%s' not found".formatted(id));
    }
}