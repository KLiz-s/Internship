package com.klyndyuk.paymentservice.exception;

public record ErrorResponse(int status, String message) {
}
