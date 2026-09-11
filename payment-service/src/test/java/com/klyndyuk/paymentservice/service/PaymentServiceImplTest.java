package com.klyndyuk.paymentservice.service;

import com.klyndyuk.paymentservice.client.PaymentClient;
import com.klyndyuk.paymentservice.dto.request.CreatePaymentRequest;
import com.klyndyuk.paymentservice.dto.response.PaymentResponse;
import com.klyndyuk.paymentservice.entity.Payment;
import com.klyndyuk.paymentservice.kafka.PaymentEventProducer;
import com.klyndyuk.paymentservice.kafka.event.CreatePaymentEvent;
import com.klyndyuk.paymentservice.mapper.PaymentMapper;
import com.klyndyuk.paymentservice.repository.PaymentRepository;
import com.klyndyuk.paymentservice.service.impl.PaymentServiceImpl;
import com.klyndyuk.paymentservice.util.TestConstants;
import com.klyndyuk.paymentservice.util.TestPayments;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private PaymentEventProducer paymentEventProducer;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void createPayment_shouldCreateSuccessfulPayment() {
        CreatePaymentRequest request = TestPayments.createPaymentRequest();
        Payment payment = TestPayments.createPayment();
        PaymentResponse response = TestPayments.createPaymentResponse();

        given(paymentMapper.fromCreatePaymentRequest(request))
                .willReturn(payment);
        given(paymentClient.makePayment(payment.getAmount()))
                .willReturn(true);
        given(paymentRepository.save(payment))
                .willReturn(payment);
        given(paymentMapper.toPaymentResponse(payment))
                .willReturn(response);

        PaymentResponse result = paymentService.createPayment(
                request,
                userDetails(TestConstants.USER_ID)
        );

        assertThat(result).isSameAs(response);
        assertThat(payment.getId()).isNotNull();
        assertThat(payment.getUserId()).isEqualTo(TestConstants.USER_ID);
        assertThat(payment.getStatus()).isEqualTo("SUCCESS");
        assertThat(payment.getTimestamp()).isNotNull();

        then(paymentRepository).should().save(payment);
        then(paymentEventProducer).should().send(
                new CreatePaymentEvent(
                        payment.getId(),
                        payment.getOrderId(),
                        "SUCCESS"
                )
        );
    }

    @Test
    void createPayment_shouldCreateFailedPaymentWhenPaymentClientReturnsFalse() {
        CreatePaymentRequest request = TestPayments.createPaymentRequest();
        Payment payment = TestPayments.createPayment();
        PaymentResponse response = TestPayments.createPaymentResponse();

        given(paymentMapper.fromCreatePaymentRequest(request))
                .willReturn(payment);
        given(paymentClient.makePayment(payment.getAmount()))
                .willReturn(false);
        given(paymentRepository.save(payment))
                .willReturn(payment);
        given(paymentMapper.toPaymentResponse(payment))
                .willReturn(response);

        PaymentResponse result = paymentService.createPayment(
                request,
                userDetails(TestConstants.USER_ID)
        );

        assertThat(result).isSameAs(response);
        assertThat(payment.getStatus()).isEqualTo("FAILED");

        then(paymentRepository).should().save(payment);
        then(paymentEventProducer).should().send(
                new CreatePaymentEvent(
                        payment.getId(),
                        payment.getOrderId(),
                        "FAILED"
                )
        );
    }

    @Test
    void getByUserId_shouldReturnPaymentsForCurrentUser() {
        PageRequest pageable = PageRequest.of(0, 10);

        Payment payment = TestPayments.createPayment();
        PaymentResponse response = TestPayments.createPaymentResponse();

        given(paymentRepository.findByUserId(
                TestConstants.USER_ID,
                pageable
        )).willReturn(new PageImpl<>(List.of(payment)));

        given(paymentMapper.toPaymentResponse(payment))
                .willReturn(response);

        Page<PaymentResponse> result = paymentService.getByUserId(
                userDetails(TestConstants.USER_ID),
                pageable
        );

        assertThat(result.getContent())
                .containsExactly(response);

        then(paymentRepository).should()
                .findByUserId(TestConstants.USER_ID, pageable);
    }

    @Test
    void getByOrderId_shouldReturnAllPaymentsForAdmin() {
        PageRequest pageable = PageRequest.of(0, 10);

        Payment payment = TestPayments.createPayment();
        PaymentResponse response = TestPayments.createPaymentResponse();

        given(paymentRepository.findByOrderIdAndUserId(
                TestConstants.ORDER_ID,
                TestConstants.USER_ID,
                pageable
        )).willReturn(new PageImpl<>(List.of(payment)));

        given(paymentMapper.toPaymentResponse(payment))
                .willReturn(response);

        Page<PaymentResponse> result = paymentService.getByOrderId(
                TestConstants.ORDER_ID,
                userDetails(
                        TestConstants.USER_ID,
                        "ROLE_ADMIN"
                ),
                pageable
        );

        assertThat(result.getContent())
                .containsExactly(response);

        then(paymentRepository).should()
                .findByOrderIdAndUserId(TestConstants.ORDER_ID, TestConstants.USER_ID, pageable);

        then(paymentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void getByOrderId_shouldReturnOnlyCurrentUserPaymentsForUser() {
        PageRequest pageable = PageRequest.of(0, 10);

        Payment payment = TestPayments.createPayment();
        PaymentResponse response = TestPayments.createPaymentResponse();

        given(paymentRepository.findByOrderIdAndUserId(
                TestConstants.ORDER_ID,
                TestConstants.USER_ID,
                pageable
        )).willReturn(new PageImpl<>(List.of(payment)));

        given(paymentMapper.toPaymentResponse(payment))
                .willReturn(response);

        Page<PaymentResponse> result = paymentService.getByOrderId(
                TestConstants.ORDER_ID,
                userDetails(TestConstants.USER_ID),
                pageable
        );

        assertThat(result.getContent())
                .containsExactly(response);

        then(paymentRepository).should()
                .findByOrderIdAndUserId(
                        TestConstants.ORDER_ID,
                        TestConstants.USER_ID,
                        pageable
                );

        then(paymentRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void getByStatus_shouldReturnPaymentsWithStatus() {
        PageRequest pageable = PageRequest.of(0, 10);

        Payment payment = TestPayments.createPayment();
        PaymentResponse response = TestPayments.createPaymentResponse();

        given(paymentRepository.findByStatus(
                "SUCCESS",
                pageable
        )).willReturn(new PageImpl<>(List.of(payment)));

        given(paymentMapper.toPaymentResponse(payment))
                .willReturn(response);

        Page<PaymentResponse> result = paymentService.getByStatus(
                "SUCCESS",
                pageable
        );

        assertThat(result.getContent())
                .containsExactly(response);

        then(paymentRepository).should()
                .findByStatus("SUCCESS", pageable);
    }

    @Test
    void getTotalSumByCurrentUserIdByDateRange_shouldReturnSum() {
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        Instant end = Instant.parse("2026-01-31T23:59:59Z");

        Payment first = TestPayments.createPayment(
                new BigDecimal("20.00")
        );
        Payment second = TestPayments.createPayment(
                new BigDecimal("30.50")
        );

        given(paymentRepository.findByUserIdAndTimestampBetween(
                TestConstants.USER_ID,
                start,
                end
        )).willReturn(List.of(first, second));

        BigDecimal result =
                paymentService.getTotalSumByCurrentUserIdByDateRange(
                        start,
                        end,
                        userDetails(TestConstants.USER_ID)
                );

        assertThat(result)
                .isEqualByComparingTo("50.50");
    }

    @Test
    void getTotalForAllUserIdByDateRange_shouldReturnSumForAllUsers() {
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        Instant end = Instant.parse("2026-01-31T23:59:59Z");

        Payment first = TestPayments.createPayment(
                new BigDecimal("20.00")
        );
        Payment second = TestPayments.createPayment(
                new BigDecimal("30.50")
        );

        given(paymentRepository.findByTimestampBetween(start, end))
                .willReturn(List.of(first, second));

        BigDecimal result =
                paymentService.getTotalForAllUserIdByDateRange(
                        start,
                        end
                );

        assertThat(result)
                .isEqualByComparingTo("50.50");
    }

    @Test
    void getTotalSumByCurrentUserIdByDateRange_shouldReturnZeroWhenNoPayments() {
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        Instant end = Instant.parse("2026-01-31T23:59:59Z");

        given(paymentRepository.findByUserIdAndTimestampBetween(
                TestConstants.USER_ID,
                start,
                end
        )).willReturn(List.of());

        BigDecimal result =
                paymentService.getTotalSumByCurrentUserIdByDateRange(
                        start,
                        end,
                        userDetails(TestConstants.USER_ID)
                );

        assertThat(result)
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    private UserDetails userDetails(UUID userId) {
        return userDetails(userId, "ROLE_USER");
    }

    private UserDetails userDetails(UUID userId, String role) {
        UserDetails userDetails = mock(UserDetails.class);

        given(userDetails.getUsername())
                .willReturn(userId.toString());

        return userDetails;
    }
}