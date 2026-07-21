package com.digitaltwin.platform.exception;

/**
 * Thrown for invalid client input or violated business rules that map
 * cleanly to HTTP 400 (e.g. duplicate email on register, expired token).
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
