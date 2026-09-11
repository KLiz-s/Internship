package com.klyndyuk.authservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserRequest {

    @NotNull
    private UUID id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String surname;

    @Past
    @NotNull
    private LocalDate birthDate;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

}