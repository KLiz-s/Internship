package com.klyndyuk.paymentservice.kafka.event;

import java.util.UUID;

public record CreatePaymentEvent(
        UUID paymentId,
        UUID orderId,
        String status
) {
}