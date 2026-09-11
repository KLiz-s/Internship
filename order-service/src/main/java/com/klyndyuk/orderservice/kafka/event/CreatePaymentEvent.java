package com.klyndyuk.orderservice.kafka.event;

import java.util.UUID;

public record CreatePaymentEvent(
        UUID paymentId,
        UUID orderId,
        String status
) {
}