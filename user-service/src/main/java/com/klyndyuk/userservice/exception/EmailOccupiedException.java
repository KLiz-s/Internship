package com.klyndyuk.userservice.exception;

public class EmailOccupiedException extends RuntimeException {
    public EmailOccupiedException(String email) {
        super("Email '%s' is already occupied".formatted(email));
    }
}
