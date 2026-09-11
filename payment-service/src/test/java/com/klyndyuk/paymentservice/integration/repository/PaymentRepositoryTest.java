package com.klyndyuk.paymentservice.integration.repository;

import com.klyndyuk.paymentservice.entity.Payment;
import com.klyndyuk.paymentservice.repository.PaymentRepository;
import com.klyndyuk.paymentservice.util.TestConstants;
import com.klyndyuk.paymentservice.util.TestPayments;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void findByOrderId_shouldReturnPaymentsForOrder() {
        Payment payment = paymentRepository.save(
                TestPayments.createPayment()
        );

        Page<Payment> result = paymentRepository.findByOrderId(
                TestConstants.ORDER_ID,
                PageRequest.of(0, 10)
        );

        assertThat(result.getContent())
                .singleElement()
                .satisfies(p ->
                        assertThat(p.getId()).isEqualTo(payment.getId())
                );
    }

    @Test
    void findByOrderIdAndUserId_shouldReturnOnlyUserPayments() {
        paymentRepository.save(TestPayments.createPayment());

        Payment otherUserPayment = TestPayments.createPayment();
        otherUserPayment.setId(UUID.randomUUID());
        otherUserPayment.setUserId(TestConstants.SECOND_USER_ID);

        paymentRepository.save(otherUserPayment);

        Page<Payment> result =
                paymentRepository.findByOrderIdAndUserId(
                        TestConstants.ORDER_ID,
                        TestConstants.USER_ID,
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent())
                .singleElement()
                .satisfies(p ->
                        assertThat(p.getUserId())
                                .isEqualTo(TestConstants.USER_ID)
                );
    }

    @Test
    void findByUserId_shouldReturnOnlyUserPayments() {
        paymentRepository.save(TestPayments.createPayment());

        Payment other = TestPayments.createPayment();
        other.setId(UUID.randomUUID());
        other.setUserId(TestConstants.SECOND_USER_ID);
        paymentRepository.save(other);

        Page<Payment> result =
                paymentRepository.findByUserId(
                        TestConstants.USER_ID,
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent())
                .singleElement()
                .satisfies(p ->
                        assertThat(p.getUserId())
                                .isEqualTo(TestConstants.USER_ID)
                );
    }

    @Test
    void findByStatus_shouldReturnPaymentsWithStatus() {
        Payment success = TestPayments.createPayment();
        success.setStatus("SUCCESS");

        Payment failed = TestPayments.createPayment();
        failed.setId(UUID.randomUUID());
        failed.setStatus("FAILED");

        paymentRepository.save(success);
        paymentRepository.save(failed);

        Page<Payment> result =
                paymentRepository.findByStatus(
                        "SUCCESS",
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent())
                .singleElement()
                .satisfies(p ->
                        assertThat(p.getStatus())
                                .isEqualTo("SUCCESS")
                );
    }

    @Test
    void findByUserIdAndTimestampBetween_shouldFilterByUserAndDate() {
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        Instant end = Instant.parse("2026-01-31T23:59:59Z");

        Payment inside = TestPayments.createPayment();
        inside.setTimestamp(
                Instant.parse("2026-01-10T12:00:00Z")
        );

        Payment outside = TestPayments.createPayment();
        outside.setId(UUID.randomUUID());
        outside.setTimestamp(
                Instant.parse("2026-02-10T12:00:00Z")
        );

        paymentRepository.save(inside);
        paymentRepository.save(outside);

        List<Payment> result =
                paymentRepository.findByUserIdAndTimestampBetween(
                        TestConstants.USER_ID,
                        start,
                        end
                );

        assertThat(result)
                .singleElement()
                .satisfies(payment ->
                        assertThat(payment.getId())
                                .isEqualTo(inside.getId())
                );
    }

    @Test
    void findByTimestampBetween_shouldReturnPaymentsInDateRange() {
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        Instant end = Instant.parse("2026-01-31T23:59:59Z");

        Payment inside = TestPayments.createPayment();
        inside.setTimestamp(
                Instant.parse("2026-01-10T12:00:00Z")
        );

        Payment outside = TestPayments.createPayment();
        outside.setId(UUID.randomUUID());
        outside.setTimestamp(
                Instant.parse("2026-02-10T12:00:00Z")
        );

        paymentRepository.save(inside);
        paymentRepository.save(outside);

        List<Payment> result =
                paymentRepository.findByTimestampBetween(start, end);

        assertThat(result)
                .singleElement()
                .satisfies(payment ->
                        assertThat(payment.getId())
                                .isEqualTo(inside.getId())
                );
    }
}