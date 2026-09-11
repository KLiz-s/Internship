package com.klyndyuk.paymentservice.controller;

import com.klyndyuk.paymentservice.dto.request.CreatePaymentRequest;
import com.klyndyuk.paymentservice.dto.response.PaymentResponse;
import com.klyndyuk.paymentservice.service.interfaces.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/create")
    public PaymentResponse createPayment(@Valid @RequestBody CreatePaymentRequest createPaymentRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return paymentService.createPayment(createPaymentRequest, userDetails);
    }

    @GetMapping("/user")
    public Page<PaymentResponse> getPaymentsByUserId(@AuthenticationPrincipal UserDetails userDetails, Pageable pageable) {
        return paymentService.getByUserId(userDetails, pageable);
    }

    @GetMapping("/order/{orderId}")
    public Page<PaymentResponse> getPaymentsByOrderId(@PathVariable UUID orderId, @AuthenticationPrincipal UserDetails userDetails, Pageable pageable) {
        return paymentService.getByOrderId(orderId, userDetails, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status/{status}")
    public Page<PaymentResponse> getPaymentsByStatus(@PathVariable String status, Pageable pageable) {
        return paymentService.getByStatus(status, pageable);
    }

    @GetMapping("/total-sum")
    public BigDecimal getTotalSumByCurrentUserIdByDateRange(@RequestParam("startDate") Instant startDate,
                                                            @RequestParam("endDate") Instant endDate,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        return paymentService.getTotalSumByCurrentUserIdByDateRange(startDate, endDate, userDetails);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/total-sum/all-users")
    public BigDecimal getTotalForAllUserIdByDateRange(@RequestParam("startDate") Instant startDate,
                                                      @RequestParam("endDate") Instant endDate) {
        return paymentService.getTotalForAllUserIdByDateRange(startDate, endDate);
    }
}
