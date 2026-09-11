package com.klyndyuk.paymentservice.integration.controller;

import com.klyndyuk.paymentservice.dto.request.CreatePaymentRequest;
import com.klyndyuk.paymentservice.dto.response.PaymentResponse;
import com.klyndyuk.paymentservice.service.interfaces.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerTest extends BaseControllerTest {

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void createPayment_shouldReturnPayment() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(paymentService.createPayment(
                any(CreatePaymentRequest.class),
                any(UserDetails.class)
        )).thenReturn(new PaymentResponse());

        mockMvc.perform(post("/api/payments/create")
                        .header(HttpHeaders.AUTHORIZATION, userBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "orderId": "%s",
                                    "amount": 100.00
                                }
                                """.formatted(orderId)))
                .andExpect(status().isOk());

        verify(paymentService).createPayment(
                any(CreatePaymentRequest.class),
                any(UserDetails.class)
        );
    }

    @Test
    void getPaymentsByUserId_shouldReturnPage() throws Exception {
        Page<PaymentResponse> page =
                new PageImpl<>(List.of(new PaymentResponse()));

        when(paymentService.getByUserId(
                any(UserDetails.class),
                any(Pageable.class)
        )).thenReturn(page);

        mockMvc.perform(get("/api/payments/user")
                        .header(HttpHeaders.AUTHORIZATION, userBearerToken()))
                .andExpect(status().isOk());

        verify(paymentService).getByUserId(
                any(UserDetails.class),
                any(Pageable.class)
        );
    }

    @Test
    void getPaymentsByOrderId_shouldReturnPage() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(paymentService.getByOrderId(
                eq(orderId),
                any(UserDetails.class),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(List.of(new PaymentResponse()))
        );

        mockMvc.perform(get("/api/payments/order/{orderId}", orderId)
                        .header(HttpHeaders.AUTHORIZATION, userBearerToken()))
                .andExpect(status().isOk());

        verify(paymentService).getByOrderId(
                eq(orderId),
                any(UserDetails.class),
                any(Pageable.class)
        );
    }

    @Test
    void getPaymentsByStatus_shouldReturnPage() throws Exception {
        when(paymentService.getByStatus(
                eq("SUCCESS"),
                any(Pageable.class)
        )).thenReturn(
                new PageImpl<>(List.of(new PaymentResponse()))
        );

        mockMvc.perform(get("/api/payments/status/SUCCESS")
                        .header(HttpHeaders.AUTHORIZATION, adminBearerToken()))
                .andExpect(status().isOk());

        verify(paymentService).getByStatus(
                eq("SUCCESS"),
                any(Pageable.class)
        );
    }

    @Test
    void getTotalSumByCurrentUserIdByDateRange_shouldReturnSum() throws Exception {
        Instant startDate =
                Instant.parse("2026-01-01T00:00:00Z");
        Instant endDate =
                Instant.parse("2026-01-31T23:59:59Z");

        when(paymentService.getTotalSumByCurrentUserIdByDateRange(
                eq(startDate),
                eq(endDate),
                any(UserDetails.class)
        )).thenReturn(new BigDecimal("150.00"));

        mockMvc.perform(get("/api/payments/total-sum")
                        .header(HttpHeaders.AUTHORIZATION, userBearerToken())
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("150.00"));

        verify(paymentService).getTotalSumByCurrentUserIdByDateRange(
                eq(startDate),
                eq(endDate),
                any(UserDetails.class)
        );
    }

    @Test
    void getTotalForAllUsersByDateRange_shouldReturnSum() throws Exception {
        Instant startDate =
                Instant.parse("2026-01-01T00:00:00Z");
        Instant endDate =
                Instant.parse("2026-01-31T23:59:59Z");

        when(paymentService.getTotalForAllUserIdByDateRange(
                eq(startDate),
                eq(endDate)
        )).thenReturn(new BigDecimal("500.00"));

        mockMvc.perform(get("/api/payments/total-sum/all-users")
                        .header(HttpHeaders.AUTHORIZATION, adminBearerToken())
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("500.00"));

        verify(paymentService).getTotalForAllUserIdByDateRange(
                eq(startDate),
                eq(endDate)
        );
    }
}