package com.klyndyuk.userservice.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("User is not authorised");
    }
}
