package com.klyndyuk.paymentservice.service.impl;

import com.klyndyuk.paymentservice.client.PaymentClient;
import com.klyndyuk.paymentservice.dto.request.CreatePaymentRequest;
import com.klyndyuk.paymentservice.dto.response.PaymentResponse;
import com.klyndyuk.paymentservice.entity.Payment;
import com.klyndyuk.paymentservice.kafka.PaymentEventProducer;
import com.klyndyuk.paymentservice.kafka.event.CreatePaymentEvent;
import com.klyndyuk.paymentservice.mapper.PaymentMapper;
import com.klyndyuk.paymentservice.repository.PaymentRepository;
import com.klyndyuk.paymentservice.service.interfaces.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentClient paymentClient;
    private final PaymentEventProducer paymentEventProducer;

    @Override
    public PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest, UserDetails userDetails) {
        Payment payment = paymentMapper.fromCreatePaymentRequest(createPaymentRequest);
        payment.setId(UUID.randomUUID());
        payment.setTimestamp(Instant.now());
        payment.setUserId(UUID.fromString(userDetails.getUsername()));
        boolean isPaymentSuccessful = paymentClient.makePayment(payment.getAmount());
        payment.setStatus(isPaymentSuccessful ? "SUCCESS" : "FAILED");
        paymentRepository.save(payment);
        paymentEventProducer.send(new CreatePaymentEvent(payment.getId(), payment.getOrderId(), payment.getStatus()));
        return paymentMapper.toPaymentResponse(payment);
    }

    @Override
    public Page<PaymentResponse> getByUserId(UserDetails userDetails, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findByUserId(UUID.fromString(userDetails.getUsername()), pageable);
        return payments.map(paymentMapper::toPaymentResponse);
    }

    @Override
    public Page<PaymentResponse> getByOrderId(UUID orderId, UserDetails userDetails, Pageable pageable) {
        if (userDetails.getAuthorities().stream().anyMatch(auth -> Objects.equals(auth.getAuthority(), "ROLE_ADMIN"))) {
            Page<Payment> payments = paymentRepository.findByOrderId(orderId, pageable);
            return payments.map(paymentMapper::toPaymentResponse);
        }
        Page<Payment> payments = paymentRepository.findByOrderIdAndUserId(orderId, UUID.fromString(userDetails.getUsername()), pageable);
        return payments.map(paymentMapper::toPaymentResponse);
    }

    @Override
    public Page<PaymentResponse> getByStatus(String status, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findByStatus(status, pageable);
        return payments.map(paymentMapper::toPaymentResponse);
    }

    @Override
    public BigDecimal getTotalSumByCurrentUserIdByDateRange(Instant startDate, Instant endDate, UserDetails userDetails) {
        List<Payment> payments = paymentRepository.findByUserIdAndTimestampBetween(UUID.fromString(userDetails.getUsername()), startDate, endDate);
        return payments.stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalForAllUserIdByDateRange(Instant startDate, Instant endDate) {
        List<Payment> payments = paymentRepository.findByTimestampBetween(startDate, endDate);
        return payments.stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
