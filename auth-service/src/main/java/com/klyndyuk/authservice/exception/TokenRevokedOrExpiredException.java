package com.klyndyuk.authservice.exception;

public class TokenRevokedOrExpiredException extends RuntimeException{
    public TokenRevokedOrExpiredException() {
        super("Token is revoked or expired");
    }
}