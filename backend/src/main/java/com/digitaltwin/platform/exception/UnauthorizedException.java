package com.digitaltwin.platform.exception;

/**
 * Thrown when a request is understood but the caller lacks valid
 * credentials or permission (e.g. wrong password, invalid/expired
 * refresh token, account not verified). Mapped to HTTP 401.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
