package com.klyndyuk.authservice.controller;

import com.klyndyuk.authservice.dto.request.LoginRequest;
import com.klyndyuk.authservice.dto.request.RefreshRequest;
import com.klyndyuk.authservice.dto.request.RegistrationRequest;
import com.klyndyuk.authservice.dto.request.ValidateTokenRequest;
import com.klyndyuk.authservice.dto.response.TwoTokenResponse;
import com.klyndyuk.authservice.security.entity.MyUserDetailsWithId;
import com.klyndyuk.authservice.security.entity.UserDetailsWithId;
import com.klyndyuk.authservice.service.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        authService.register(registrationRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/login")
    public ResponseEntity<TwoTokenResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) throws NoSuchAlgorithmException {
        TwoTokenResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<TwoTokenResponse> refreshToken(@Valid @RequestBody RefreshRequest token) throws NoSuchAlgorithmException {
        TwoTokenResponse response = authService.refresh(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/logout")
    public void logout(@AuthenticationPrincipal UserDetailsWithId user) {
        authService.logout(user);
    }

    @PostMapping("/validate")
    public ResponseEntity<Void> validateToken(
            @Valid @RequestBody ValidateTokenRequest request) {

        authService.validateToken(request.getToken());
        return ResponseEntity.ok().build();
    }
}
