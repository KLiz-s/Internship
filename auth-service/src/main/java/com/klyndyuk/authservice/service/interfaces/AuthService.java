package com.klyndyuk.authservice.service.interfaces;

import com.klyndyuk.authservice.dto.request.LoginRequest;
import com.klyndyuk.authservice.dto.request.RefreshRequest;
import com.klyndyuk.authservice.dto.request.RegistrationRequest;
import com.klyndyuk.authservice.dto.response.TwoTokenResponse;
import com.klyndyuk.authservice.security.entity.UserDetailsWithId;

import java.security.NoSuchAlgorithmException;

public interface AuthService {
    void register(RegistrationRequest request);

    TwoTokenResponse login(LoginRequest loginRequest) throws NoSuchAlgorithmException;

    TwoTokenResponse refresh(RefreshRequest token) throws NoSuchAlgorithmException;

    void logout(UserDetailsWithId user);

    void validateToken(String token);
}