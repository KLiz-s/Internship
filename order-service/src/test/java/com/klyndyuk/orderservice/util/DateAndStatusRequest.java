package com.klyndyuk.orderservice.util;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class DateAndStatusRequest {

    @NotNull
    private Instant fromDate;

    @NotNull
    private Instant toDate;

    @NotBlank
    private String status;

}
