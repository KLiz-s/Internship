package com.klyndyuk.authservice.service.impl;

import com.klyndyuk.authservice.service.interfaces.RefreshTokenHasher;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class RefreshTokenHasherImpl implements RefreshTokenHasher {
    @Override
    public String hash(String token) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder()
                .encodeToString(hash);
    }
}
