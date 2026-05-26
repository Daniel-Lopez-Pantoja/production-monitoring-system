package com.production.monitoring.controller;

import com.production.monitoring.dto.ManufacturingTestStatusRequest;
import com.production.monitoring.model.entity.ManufacturingTest;
import com.production.monitoring.service.ManufacturingTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manufacturing-tests")
@RequiredArgsConstructor
public class ManufacturingTestController {
    private final ManufacturingTestService manufacturingTestService;

    @GetMapping("/server/{serverId}")
    public List<ManufacturingTest> findByServer(@PathVariable Long serverId) {
        return manufacturingTestService.findByServer(serverId);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','ENGINEER','TECHNICIAN')")
    public ManufacturingTest updateStatus(@PathVariable Long id, @Valid @RequestBody ManufacturingTestStatusRequest request) {
        return manufacturingTestService.updateStatus(id, request);
    }
}
