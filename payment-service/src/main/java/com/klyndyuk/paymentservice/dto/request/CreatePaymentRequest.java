package com.klyndyuk.paymentservice.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
public class CreatePaymentRequest {

    @NotNull
    private UUID orderId;
    @NotNull
    private BigDecimal amount;

}
