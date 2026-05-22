package com.production.monitoring.model.enums;

/**
 * Estados principales por los que avanza un servidor durante producción y pruebas.
 */
public enum ServerStatus {
    PENDING_OS,
    OS_INSTALLED,
    READY_FOR_TEST,
    IN_TEST,
    FAILED,
    DEBUG,
    RETEST,
    PASSED,
    RELEASED
}
