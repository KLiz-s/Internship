package com.klyndyuk.authservice.exception;

public class LoginOccupiedException extends RuntimeException {
    public LoginOccupiedException(String login) {
        super("Login '%s' is already occupied".formatted(login));
    }
}
