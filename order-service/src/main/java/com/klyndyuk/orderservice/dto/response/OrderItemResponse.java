package com.klyndyuk.orderservice.dto.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemResponse {
    @NotBlank
    private String name;

    @NotNull
    private BigDecimal price;

    @Min(1)
    private Integer quantity;
}
