package com.production.monitoring.model.enums;

/**
 * Flujo de atención de una falla desde que se abre hasta que se cierra.
 */
public enum FailureStatus {
    OPEN,
    IN_PROGRESS,
    FIXED,
    RETEST_REQUIRED,
    CLOSED
}
