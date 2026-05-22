package com.production.monitoring.service;

import com.production.monitoring.dto.FailureRequest;
import com.production.monitoring.exception.ResourceNotFoundException;
import com.production.monitoring.model.entity.Failure;
import com.production.monitoring.repository.FailureRepository;
import com.production.monitoring.repository.ProductionServerRepository;
import com.production.monitoring.repository.TestCatalogRepository;
import com.production.monitoring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Registra y consulta fallas detectadas durante las pruebas.
 */
@Service
@RequiredArgsConstructor
public class FailureService {
    private final FailureRepository failureRepository;
    private final ProductionServerRepository serverRepository;
    private final TestCatalogRepository testCatalogRepository;
    private final UserRepository userRepository;

    public List<Failure> findAll() {
        return failureRepository.findAll();
    }

    /**
     * Crea una falla ligada a un servidor y una prueba del catálogo.
     */
    public Failure create(FailureRequest request) {
        Failure failure = new Failure();
        failure.setServer(serverRepository.findById(request.serverId()).orElseThrow(() -> new ResourceNotFoundException("Servidor no encontrado.")));
        failure.setTestCatalog(testCatalogRepository.findById(request.testCatalogId()).orElseThrow(() -> new ResourceNotFoundException("Prueba no encontrada.")));
        failure.setDescription(request.description());
        failure.setSeverity(request.severity());
        failure.setStatus(request.status());
        failure.setCorrectiveAction(request.correctiveAction());
        failure.setAssignedTechnician(request.assignedTechnicianId() == null ? null : userRepository.findById(request.assignedTechnicianId()).orElseThrow());
        failure.setDetectedAt(request.detectedAt() == null ? LocalDateTime.now() : request.detectedAt());
        failure.setClosedAt(request.closedAt());
        failure.setComments(request.comments());
        failure.setEvidenceLog(request.evidenceLog());
        return failureRepository.save(failure);
    }
}
