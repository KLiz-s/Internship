package com.klyndyuk.userservice.dto.response;

import jakarta.validation.constraints.NegativeOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PaymentCardResponse {
    UUID id;
    String number;
    String holder;
    LocalDate expirationDate;
    boolean active;
}
