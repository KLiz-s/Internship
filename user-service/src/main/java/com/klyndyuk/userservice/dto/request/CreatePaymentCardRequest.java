package com.klyndyuk.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreatePaymentCardRequest {
    @NotNull
    private UUID userId;

    @NotBlank
    @Size(max = 19)
    private String number;

    @NotBlank
    @Size(max = 100)
    private String holder;

    @NotNull
    private LocalDate expirationDate;
}
