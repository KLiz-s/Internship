package com.klyndyuk.paymentservice.util;

import com.klyndyuk.paymentservice.dto.request.CreatePaymentRequest;
import com.klyndyuk.paymentservice.dto.response.PaymentResponse;
import com.klyndyuk.paymentservice.entity.Payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class TestPayments {

    private TestPayments() {
    }

    public static CreatePaymentRequest createPaymentRequest() {
        CreatePaymentRequest request = new CreatePaymentRequest();

        request.setOrderId(TestConstants.ORDER_ID);
        request.setAmount(TestConstants.PAYMENT_AMOUNT);

        return request;
    }

    public static Payment createPayment() {
        Payment payment = new Payment();

        payment.setId(TestConstants.PAYMENT_ID);
        payment.setOrderId(TestConstants.ORDER_ID);
        payment.setUserId(TestConstants.USER_ID);
        payment.setStatus(TestConstants.PAYMENT_STATUS_SUCCESS);
        payment.setTimestamp(TestConstants.PAYMENT_TIMESTAMP);
        payment.setAmount(TestConstants.PAYMENT_AMOUNT);

        return payment;
    }

    public static Payment createPayment(BigDecimal amount) {
        Payment payment = createPayment();
        payment.setAmount(amount);
        return payment;
    }

    public static PaymentResponse createPaymentResponse() {
        return new PaymentResponse();
    }

    public static Payment createPayment(
            UUID paymentId,
            UUID userId,
            UUID orderId,
            String status,
            BigDecimal amount,
            Instant timestamp
    ) {
        Payment payment = new Payment();

        payment.setId(paymentId);
        payment.setUserId(userId);
        payment.setOrderId(orderId);
        payment.setStatus(status);
        payment.setAmount(amount);
        payment.setTimestamp(timestamp);

        return payment;
    }
}