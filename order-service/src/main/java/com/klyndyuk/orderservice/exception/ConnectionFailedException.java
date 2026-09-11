package com.klyndyuk.orderservice.exception;

public class ConnectionFailedException extends RuntimeException {
    public ConnectionFailedException(String message) {
        super(message);
    }
}
