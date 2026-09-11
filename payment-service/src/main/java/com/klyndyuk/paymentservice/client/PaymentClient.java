package com.klyndyuk.paymentservice.client;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentClient {
    public boolean makePayment(BigDecimal amount) {
        return Math.random() >= 0.5;
    }
}
