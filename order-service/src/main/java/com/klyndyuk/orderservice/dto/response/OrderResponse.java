package com.klyndyuk.orderservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {
    private List<OrderItemResponse> items;

    private UUID id;

    private UUID userId;

    private String status;

    private BigDecimal totalPrice;

    private UserResponse user;
}
