package com.digitaltwin.platform.exception;

/**
 * Thrown when a requested entity (by id or unique key) does not exist.
 * Mapped to HTTP 404 by GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException forEntity(String entityName, Object identifier) {
        return new ResourceNotFoundException(entityName + " not found with identifier: " + identifier);
    }
}
