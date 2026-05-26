package com.production.monitoring.service;

import org.springframework.stereotype.Service;

@Service
public class ValidationDurationService {
    public DurationRange durationForTestName(String testName) {
        String normalizedName = normalize(testName);
        if (normalizedName.contains("operating system")) return new DurationRange(15, 45);
        if (normalizedName.contains("post") || normalizedName.contains("power on") || normalizedName.contains("power-on")) return new DurationRange(5, 10);
        if (normalizedName.contains("bmc") || normalizedName.contains("ipmi") || normalizedName.contains("redfish")) return new DurationRange(10, 20);
        if (normalizedName.contains("bios") || normalizedName.contains("firmware")) return new DurationRange(10, 25);
        if (normalizedName.contains("memory")) return new DurationRange(30, 90);
        if (normalizedName.contains("cpu")) return new DurationRange(30, 75);
        if (normalizedName.contains("storage")) return new DurationRange(20, 60);
        if (normalizedName.contains("ethernet")) return new DurationRange(5, 15);
        if (normalizedName.contains("network traffic")) return new DurationRange(10, 30);
        if (normalizedName.contains("led")) return new DurationRange(3, 5);
        if (normalizedName.contains("power supply") || normalizedName.contains("pdu")) return new DurationRange(10, 20);
        if (normalizedName.contains("fan")) return new DurationRange(5, 15);
        if (normalizedName.contains("thermal stability")) return new DurationRange(45, 120);
        if (normalizedName.contains("thermal") || normalizedName.contains("burn in") || normalizedName.contains("burn-in")) return new DurationRange(60, 180);
        if (normalizedName.contains("stress")) return new DurationRange(45, 120);
        if (normalizedName.contains("inventory")) return new DurationRange(5, 15);
        if (normalizedName.contains("usb")) return new DurationRange(5, 10);
        if (normalizedName.contains("sel") || normalizedName.contains("event log")) return new DurationRange(5, 15);
        if (normalizedName.contains("final functional")) return new DurationRange(10, 25);
        return new DurationRange(5, 15);
    }

    private String normalize(String value) {
        return String.valueOf(value == null ? "" : value)
                .toLowerCase()
                .replace("/", " ")
                .replace("_", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public record DurationRange(int estimatedMinMinutes, int estimatedMaxMinutes) {}
}
