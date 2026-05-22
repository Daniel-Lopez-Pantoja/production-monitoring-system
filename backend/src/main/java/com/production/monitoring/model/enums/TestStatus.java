package com.production.monitoring.model.enums;

/**
 * Estados disponibles para cada prueba individual de un servidor.
 */
public enum TestStatus {
    NOT_STARTED,
    RUNNING,
    PASSED,
    FAILED,
    SKIPPED,
    RETEST
}
