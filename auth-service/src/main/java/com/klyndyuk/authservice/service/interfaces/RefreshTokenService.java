package com.klyndyuk.authservice.service.interfaces;

import com.klyndyuk.authservice.entity.Credentials;

import java.security.NoSuchAlgorithmException;


public interface RefreshTokenService {
    String generateToken(Credentials credentials) throws NoSuchAlgorithmException;

    String refreshToken(String refreshToken) throws NoSuchAlgorithmException;

    String getUserIdFromToken(String token) throws NoSuchAlgorithmException;

    void logout(Credentials credentials);
}
