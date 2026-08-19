package com.klyndyuk.orderservice.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("User is not authorised");
    }
}
