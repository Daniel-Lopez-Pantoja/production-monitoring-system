package com.production.monitoring.service;

import com.production.monitoring.dto.TraceabilityRequest;
import com.production.monitoring.exception.ResourceNotFoundException;
import com.production.monitoring.model.entity.TraceabilityRecord;
import com.production.monitoring.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Gestiona la matriz de trazabilidad y sus relaciones con servidor, prueba, PDU y Raspberry.
 */
@Service
@RequiredArgsConstructor
public class TraceabilityService {
    private final TraceabilityRecordRepository traceabilityRepository;
    private final ProductionServerRepository serverRepository;
    private final TestCatalogRepository testCatalogRepository;
    private final RaspberryDeviceRepository raspberryRepository;
    private final PduRepository pduRepository;
    private final UserRepository userRepository;

    public List<TraceabilityRecord> findAll() {
        return traceabilityRepository.findAll();
    }

    /**
     * Crea un registro de trazabilidad con toda la evidencia técnica de la prueba.
     */
    public TraceabilityRecord create(TraceabilityRequest request) {
        TraceabilityRecord record = new TraceabilityRecord();
        record.setServer(serverRepository.findById(request.serverId()).orElseThrow(() -> new ResourceNotFoundException("Servidor no encontrado.")));
        record.setTestCatalog(testCatalogRepository.findById(request.testCatalogId()).orElseThrow(() -> new ResourceNotFoundException("Prueba no encontrada.")));
        record.setRaspberry(request.raspberryId() == null ? null : raspberryRepository.findById(request.raspberryId()).orElseThrow());
        record.setPdu(request.pduId() == null ? null : pduRepository.findById(request.pduId()).orElseThrow());
        record.setPduPort(request.pduPort());
        record.setColdRoom(request.coldRoom());
        record.setPhysicalLocation(request.physicalLocation());
        record.setTestStatus(request.testStatus());
        record.setResult(request.result());
        record.setDetectedFailure(request.detectedFailure());
        record.setSeverity(request.severity());
        record.setCorrectiveAction(request.correctiveAction());
        record.setResponsibleEngineer(request.responsibleEngineerId() == null ? null : userRepository.findById(request.responsibleEngineerId()).orElseThrow());
        record.setResponsibleTechnician(userRepository.findById(request.responsibleTechnicianId()).orElseThrow());
        record.setStartDate(request.startDate());
        record.setEndDate(request.endDate());
        record.setComments(request.comments());
        record.setEvidenceLogReference(request.evidenceLogReference());
        return traceabilityRepository.save(record);
    }
}
