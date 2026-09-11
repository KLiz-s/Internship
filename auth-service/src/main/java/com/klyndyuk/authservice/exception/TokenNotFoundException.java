package com.klyndyuk.authservice.exception;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException() {
        super("There is not such token");
    }
}
