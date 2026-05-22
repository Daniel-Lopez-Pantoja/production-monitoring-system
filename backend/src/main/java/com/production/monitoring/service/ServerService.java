package com.production.monitoring.service;

import com.production.monitoring.dto.ServerRequest;
import com.production.monitoring.exception.BusinessRuleException;
import com.production.monitoring.exception.ResourceNotFoundException;
import com.production.monitoring.model.entity.ProductionServer;
import com.production.monitoring.model.enums.FailureStatus;
import com.production.monitoring.model.enums.ServerStatus;
import com.production.monitoring.model.enums.TestStatus;
import com.production.monitoring.repository.FailureRepository;
import com.production.monitoring.repository.ProductionServerRepository;
import com.production.monitoring.repository.ServerTestRepository;
import com.production.monitoring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contiene reglas de negocio para crear, actualizar y liberar servidores.
 */
@Service
@RequiredArgsConstructor
public class ServerService {
    private final ProductionServerRepository serverRepository;
    private final UserRepository userRepository;
    private final FailureRepository failureRepository;
    private final ServerTestRepository serverTestRepository;

    public List<ProductionServer> findAll() {
        return serverRepository.findAll();
    }

    public ProductionServer findById(Long id) {
        return serverRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Servidor no encontrado."));
    }

    /**
     * Crea un servidor validando serial e ID interno únicos.
     */
    public ProductionServer create(ServerRequest request) {
        if (serverRepository.existsBySerialNumber(request.serialNumber())) {
            throw new BusinessRuleException("El serial ya existe.");
        }
        if (serverRepository.existsByInternalId(request.internalId())) {
            throw new BusinessRuleException("El ID interno ya existe.");
        }
        ProductionServer server = new ProductionServer();
        copy(request, server);
        return serverRepository.save(server);
    }

    /**
     * Actualiza datos y evita liberar servidores con fallas abiertas o pruebas críticas fallidas.
     */
    public ProductionServer update(Long id, ServerRequest request) {
        ProductionServer server = findById(id);
        if (request.status() == ServerStatus.RELEASED) {
            boolean hasOpenFailures = failureRepository.existsByServerIdAndStatusNot(id, FailureStatus.CLOSED);
            boolean hasCriticalFailedTests = serverTestRepository.existsByServerIdAndTestCatalogCriticalTrueAndStatus(id, TestStatus.FAILED);
            if (hasOpenFailures) throw new BusinessRuleException("No se puede liberar un servidor con fallas abiertas.");
            if (hasCriticalFailedTests) throw new BusinessRuleException("No se puede liberar un servidor con pruebas críticas fallidas.");
        }
        copy(request, server);
        return serverRepository.save(server);
    }

    public void delete(Long id) {
        serverRepository.delete(findById(id));
    }

    private void copy(ServerRequest request, ProductionServer server) {
        server.setInternalId(request.internalId());
        server.setSerialNumber(request.serialNumber());
        server.setModel(request.model());
        server.setRackNumber(request.rackNumber());
        server.setLocation(request.location());
        server.setStatus(request.status());
        server.setEntryDate(request.entryDate() == null ? LocalDateTime.now() : request.entryDate());
        server.setObservations(request.observations());
        server.setResponsibleEngineer(request.responsibleEngineerId() == null ? null : userRepository.findById(request.responsibleEngineerId()).orElseThrow());
        server.setAssignedTechnician(request.assignedTechnicianId() == null ? null : userRepository.findById(request.assignedTechnicianId()).orElseThrow());
    }
}
