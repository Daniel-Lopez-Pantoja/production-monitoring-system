package com.production.monitoring.service;

import com.production.monitoring.dto.ServerTestRequest;
import com.production.monitoring.exception.ResourceNotFoundException;
import com.production.monitoring.model.entity.ServerTest;
import com.production.monitoring.repository.ProductionServerRepository;
import com.production.monitoring.repository.ServerTestRepository;
import com.production.monitoring.repository.TestCatalogRepository;
import com.production.monitoring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Maneja resultados de pruebas ejecutadas por técnicos o ingenieros.
 */
@Service
@RequiredArgsConstructor
public class ServerTestService {
    private final ServerTestRepository serverTestRepository;
    private final ProductionServerRepository serverRepository;
    private final TestCatalogRepository testCatalogRepository;
    private final UserRepository userRepository;

    public List<ServerTest> findAll() {
        return serverTestRepository.findAll();
    }

    /**
     * Registra el estado y resultado de una prueba para un servidor.
     */
    public ServerTest create(ServerTestRequest request) {
        ServerTest test = new ServerTest();
        test.setServer(serverRepository.findById(request.serverId()).orElseThrow(() -> new ResourceNotFoundException("Server not found.")));
        test.setTestCatalog(testCatalogRepository.findById(request.testCatalogId()).orElseThrow(() -> new ResourceNotFoundException("Test not found.")));
        test.setStatus(request.status());
        test.setResult(request.result());
        test.setTechnician(userRepository.findById(request.technicianId()).orElseThrow(() -> new ResourceNotFoundException("Technician not found.")));
        test.setStartedAt(request.startedAt());
        test.setFinishedAt(request.finishedAt());
        test.setComments(request.comments());
        return serverTestRepository.save(test);
    }
}
