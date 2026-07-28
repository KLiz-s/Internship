package com.klyndyuk.authservice.service.interfaces;

import java.security.NoSuchAlgorithmException;

public interface RefreshTokenHasher {
    String hash(String token) throws NoSuchAlgorithmException;
}
