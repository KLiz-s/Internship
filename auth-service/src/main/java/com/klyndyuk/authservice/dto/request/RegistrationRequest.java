package com.klyndyuk.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegistrationRequest {
    @NotBlank
    private String login;
    @NotBlank
    private String password;
}
