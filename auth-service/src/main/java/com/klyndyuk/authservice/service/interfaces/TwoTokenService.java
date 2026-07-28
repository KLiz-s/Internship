package com.klyndyuk.authservice.service.interfaces;

import com.klyndyuk.authservice.dto.response.TwoTokenResponse;
import com.klyndyuk.authservice.entity.Credentials;

import java.security.NoSuchAlgorithmException;

public interface TwoTokenService {
    TwoTokenResponse generateTokenPair(Credentials credentials) throws NoSuchAlgorithmException;

    TwoTokenResponse updateTokenPair(String token) throws NoSuchAlgorithmException;

    void logout(Credentials credentials);
}
