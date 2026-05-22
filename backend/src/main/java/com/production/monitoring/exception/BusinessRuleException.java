package com.production.monitoring.exception;

/**
 * Excepción para reglas de negocio, por ejemplo liberar un servidor con fallas abiertas.
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
