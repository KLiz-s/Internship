package com.klyndyuk.orderservice.service.interfaces;

import com.klyndyuk.orderservice.dto.request.CreateOrderRequest;
import com.klyndyuk.orderservice.dto.request.UpdateOrderRequest;
import com.klyndyuk.orderservice.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest createOrderRequest, UserDetails userDetails);

    OrderResponse getById(UUID id, UserDetails userDetails);

    Page<OrderResponse> getAllByDateAndStatus(Instant fromDate, Instant toDate, String status, Pageable pageable);

    List<OrderResponse> getAllByUserId(UUID userId, UserDetails userDetails);

    OrderResponse update(UUID id, UpdateOrderRequest updateOrderRequest, UserDetails userDetails);

    void deleteById(UUID id, UserDetails userDetails);
}
