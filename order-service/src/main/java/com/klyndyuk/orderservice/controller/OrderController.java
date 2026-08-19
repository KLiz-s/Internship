package com.klyndyuk.orderservice.controller;

import com.klyndyuk.orderservice.dto.request.CreateOrderRequest;
import com.klyndyuk.orderservice.dto.request.UpdateOrderRequest;
import com.klyndyuk.orderservice.dto.response.OrderResponse;
import com.klyndyuk.orderservice.service.interfaces.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest createOrderRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        OrderResponse orderResponse = orderService.createOrder(createOrderRequest, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        OrderResponse orderResponse = orderService.getById(id, userDetails);
        return ResponseEntity.ok(orderResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrdersByDateAndStatus(
            @RequestParam ("fromDate") Instant fromDate,
            @RequestParam ("toDate") Instant toDate,
            @RequestParam ("status") String status,
            Pageable pageable) {
        Page<OrderResponse> orderResponses = orderService.getAllByDateAndStatus(fromDate, toDate, status, pageable);
        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getAllOrdersByUserId(@PathVariable UUID userId, @AuthenticationPrincipal UserDetails userDetails) {
        List<OrderResponse> orderResponses = orderService.getAllByUserId(userId, userDetails);
        return ResponseEntity.ok(orderResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderRequest updateOrderRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        OrderResponse orderResponse = orderService.update(id, updateOrderRequest, userDetails);
        return ResponseEntity.ok(orderResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        orderService.deleteById(id, userDetails);
        return ResponseEntity.noContent().build();
    }
}
