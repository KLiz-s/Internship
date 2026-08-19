package com.klyndyuk.orderservice.exception;

import java.io.Serializable;

public record ErrorResponse(int status, String message) {
}
