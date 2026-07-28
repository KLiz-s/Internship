package com.klyndyuk.authservice.exception;

public class CredentialsNotFoundException extends RuntimeException {
    public CredentialsNotFoundException() {
        super("Credentials not found");
    }
}
