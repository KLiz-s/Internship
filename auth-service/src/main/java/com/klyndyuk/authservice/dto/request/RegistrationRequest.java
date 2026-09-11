package com.klyndyuk.authservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RegistrationRequest {
    @NotBlank
    private String login;
    @NotBlank
    private String password;

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
