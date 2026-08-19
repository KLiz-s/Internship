package com.klyndyuk.orderservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateOrderItemRequest {
    @NotNull
    private UUID itemId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
