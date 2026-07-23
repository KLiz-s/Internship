package com.klyndyuk.userservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailsResponse {

    UUID id;
    String name;
    String surname;
    LocalDate birthDate;
    String email;
    boolean active;

    List<PaymentCardResponse> paymentCards;
}