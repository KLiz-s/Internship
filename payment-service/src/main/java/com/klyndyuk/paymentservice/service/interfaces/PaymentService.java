package com.klyndyuk.paymentservice.service.interfaces;

import com.klyndyuk.paymentservice.dto.request.CreatePaymentRequest;
import com.klyndyuk.paymentservice.dto.response.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface PaymentService {
    PaymentResponse createPayment(CreatePaymentRequest createPaymentRequest, UserDetails userDetails);

    Page<PaymentResponse> getByUserId(UserDetails userDetails, Pageable pageable);
    Page<PaymentResponse> getByOrderId(UUID orderId, UserDetails userDetails, Pageable pageable);
    Page<PaymentResponse> getByStatus(String status, Pageable pageable);

    BigDecimal getTotalSumByCurrentUserIdByDateRange(Instant startDate, Instant endDate, UserDetails userDetails);
    BigDecimal getTotalForAllUserIdByDateRange(Instant startDate, Instant endDate);
}
