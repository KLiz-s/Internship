package com.klyndyuk.authservice.exception;

public class RegistrationFailedException extends RuntimeException {
    public RegistrationFailedException() {
        super("Registration failed. Please try again later. Email may be already in use.");
    }
}
