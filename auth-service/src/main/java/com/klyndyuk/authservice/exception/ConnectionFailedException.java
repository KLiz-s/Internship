package com.klyndyuk.authservice.exception;

public class ConnectionFailedException extends RuntimeException {
    public ConnectionFailedException(String message) {
        super(message);
    }
}
