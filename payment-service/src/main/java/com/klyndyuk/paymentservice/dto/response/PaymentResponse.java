package com.klyndyuk.paymentservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
public class PaymentResponse {
    private UUID id;
    private UUID orderId;
    private UUID userId;
    private String status;
    private Instant timestamp;
    private BigDecimal amount;
}
