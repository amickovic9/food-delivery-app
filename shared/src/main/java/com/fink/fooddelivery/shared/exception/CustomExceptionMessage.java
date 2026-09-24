package com.fink.fooddelivery.shared.exception;

import java.time.Instant;

public record CustomExceptionMessage(int status, String message, Instant timestamp) {
    public CustomExceptionMessage(int status, String message) {
        this(status, message, Instant.now());
    }
}
