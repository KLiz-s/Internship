package com.klyndyuk.paymentservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "payments")
public class Payment {
    @Id
    private UUID id;

    @Field("order_id")
    private UUID orderId;

    @Field("user_id")
    private UUID userId;

    @Field("status")
    private String status;

    @Field("timestamp")
    private Instant timestamp;

    @Field("payment_amount")
    private BigDecimal amount;
}
