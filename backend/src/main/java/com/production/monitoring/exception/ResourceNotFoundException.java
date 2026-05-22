package com.production.monitoring.exception;

/**
 * Excepción para indicar que un recurso solicitado no existe.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
