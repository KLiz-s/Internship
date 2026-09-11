package com.klyndyuk.paymentservice.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("User is not authorised");
    }
}
