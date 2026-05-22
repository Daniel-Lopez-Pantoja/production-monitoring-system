package com.production.monitoring.config;

import com.production.monitoring.model.entity.*;
import com.production.monitoring.model.enums.*;
import com.production.monitoring.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Inserta datos iniciales para probar la aplicación sin cargar todo manualmente.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TestCatalogRepository testCatalogRepository;
    private final ProductionServerRepository serverRepository;
    private final FailureRepository failureRepository;
    private final PduRepository pduRepository;
    private final RaspberryDeviceRepository raspberryRepository;
    private final ServerTestRepository serverTestRepository;
    private final TraceabilityRecordRepository traceabilityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedDevices();
        seedTests();
        seedServersAndFailures();
        seedTraceabilityRecords();
    }

    private void seedUsers() {
        for (UserRole roleName : UserRole.values()) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                role.setDescription("System role for " + roleName.name() + " users.");
                roleRepository.save(role);
            }
        }
        createUser("Daniel Lopez", "admin@pms.local", "admin123", UserRole.ADMIN);
        createUser("Test Engineer", "engineer@pms.local", "engineer123", UserRole.ENGINEER);
        createUser("Debug Technician", "technician@pms.local", "tech123", UserRole.TECHNICIAN);
        createUser("Line Operator", "operator@pms.local", "operator123", UserRole.OPERATOR);
        createUser("Manufacturing Engineer", "manufacturing.engineer@pms.local", "engineer123", UserRole.ENGINEER);
        createUser("Quality Engineer", "quality.engineer@pms.local", "engineer123", UserRole.ENGINEER);
        createUser("Burn-In Technician", "burnin.tech@pms.local", "tech123", UserRole.TECHNICIAN);
    }

    private void createUser(String name, String email, String password, UserRole role) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setFullName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            userRepository.save(user);
        }
    }

    private void seedDevices() {
        createPdu("PDU-GDL-CR1-A01", "10.42.20.11", "A01", "Cold Room GDL-01");
        createPdu("PDU-GDL-CR1-A02", "10.42.20.12", "A02", "Cold Room GDL-01");
        createPdu("PDU-GDL-CR2-B01", "10.42.20.21", "B01", "Cold Room GDL-02");
        createRaspberry("RPI-GDL-A01-OSLOAD", "10.42.30.21", "A01");
        createRaspberry("RPI-GDL-A02-BURNIN", "10.42.30.22", "A02");
        createRaspberry("RPI-GDL-B01-DEBUG", "10.42.30.31", "B01");
    }

    /**
     * Crea una PDU de manufactura si todavía no existe en la base.
     */
    private void createPdu(String name, String ip, String rack, String location) {
        if (!pduRepository.existsByName(name)) {
            Pdu pdu = new Pdu();
            pdu.setName(name);
            pdu.setIpAddress(ip);
            pdu.setRack(rack);
            pdu.setLocation(location);
            pduRepository.save(pdu);
        }
    }

    /**
     * Crea una Raspberry de prueba si todavía no existe en la base.
     */
    private void createRaspberry(String name, String ip, String rack) {
        if (!raspberryRepository.existsByName(name)) {
            RaspberryDevice raspberry = new RaspberryDevice();
            raspberry.setName(name);
            raspberry.setIpAddress(ip);
            raspberry.setRack(rack);
            raspberryRepository.save(raspberry);
        }
    }

    private void seedTests() {
        List<String[]> tests = List.of(
                row("Operating System Installation", "Validates that the server can boot and install the base OS image.", "Disk or USB not detected; BIOS/UEFI boot misconfiguration; corrupted image.", true),
                row("POST / Power On Self Test", "Validates motherboard, CPU, RAM, BIOS and peripheral initialization.", "No video or POST; memory error code; reboot during initialization.", true),
                row("BMC / IPMI / Redfish", "Validates remote management sensors, logs, power control and inventory.", "BMC not responding over network; incorrect sensor values; corrupted firmware.", true),
                row("BIOS / Firmware Check", "Validates BIOS, BMC, CPLD, NIC and RAID firmware baseline.", "Incorrect firmware version; failed update; BIOS-BMC compatibility issue.", true),
                row("Memory Validation Test", "Validates DIMM stability under intensive read/write operations.", "DIMM not detected; ECC errors; uncorrectable error or thermal trip.", true),
                row("CPU Stress Test", "Validates CPU load, cores, frequency, temperature and stability.", "CPU not detected; thermal throttling; crash under stress.", true),
                row("Storage Validation Test", "Validates SSD, HDD, NVMe, RAID, SMART health and throughput.", "Drive not detected; SMART errors; low throughput from slot, cable or backplane.", true),
                row("Ethernet Network Test", "Validates link, speed, traffic stability, packet loss and throughput.", "Ethernet port without link; incorrect negotiated speed; packet loss.", false),
                row("LED Indicator Test", "Validates power, UID, NIC, fan, PSU, fault and status indicators.", "LED not turning on; incorrect LED state; intermittent GPIO or firmware behavior.", false),
                row("Power Supply / PDU Test", "Validates PSU input, voltage reporting, redundancy and PDU state.", "PSU not detected; redundancy failure; shutdown during PDU or load transition.", true),
                row("Fan Validation Test", "Validates fan RPM, PWM control, redundancy and thermal response.", "Fan not spinning; RPM out of range; fan locked at 100%.", true),
                row("Thermal / Burn-In Test", "Validates long-duration stability under load and thermal control.", "Overheating; thermal throttling; protective thermal shutdown.", true),
                row("General Stress Test", "Validates CPU, RAM, disk and network under combined workload.", "Kernel panic; unexpected reboot; intermittent failure.", true),
                row("Hardware Inventory Validation", "Validates expected CPUs, RAM, drives, NICs, PSUs, fans and serials.", "Missing component; incorrect serial or FRU; BOM mismatch.", false),
                row("USB Port Test", "Validates USB boot and stability for server installation paths.", "USB does not boot; intermittent disconnects; incorrect USB speed.", false),
                row("SEL / Event Log Review", "Validates BMC/BIOS event logs recorded during validation.", "Critical log events; false positives; SEL log full or inaccessible.", true)
        );
        tests.forEach(data -> {
            TestCatalog test = testCatalogRepository.findAll().stream()
                    .filter(existing -> existing.getName().equals(data[0]) || legacyTestName(existing.getName()).equals(data[0]))
                    .findFirst()
                    .orElseGet(TestCatalog::new);
            test.setName(data[0]);
            test.setValidates(data[1]);
            test.setPossibleFailures(data[2]);
            test.setCritical(Boolean.parseBoolean(data[3]));
            testCatalogRepository.save(test);
        });
    }

    private String[] row(String name, String validates, String failures, boolean critical) {
        return new String[]{name, validates, failures, String.valueOf(critical)};
    }

    private String legacyTestName(String name) {
        return switch (name) {
            case "Instalación de sistema operativo" -> "Operating System Installation";
            case "Prueba de memoria RAM" -> "Memory Validation Test";
            case "Prueba de CPU" -> "CPU Stress Test";
            case "Prueba de almacenamiento" -> "Storage Validation Test";
            case "Prueba de red Ethernet" -> "Ethernet Network Test";
            case "Prueba de LEDs" -> "LED Indicator Test";
            case "Prueba de fuentes de poder / PDU" -> "Power Supply / PDU Test";
            case "Prueba de fans" -> "Fan Validation Test";
            case "Prueba térmica / cuarto frío / burn-in" -> "Thermal / Burn-In Test";
            case "Stress Test General" -> "General Stress Test";
            case "Prueba de inventario HW" -> "Hardware Inventory Validation";
            case "Prueba de puertos USB" -> "USB Port Test";
            case "Logs SEL / Event Log" -> "SEL / Event Log Review";
            default -> name;
        };
    }

    private void seedServersAndFailures() {
        User engineer = userRepository.findByEmail("engineer@pms.local").orElseThrow();
        User manufacturing = userRepository.findByEmail("manufacturing.engineer@pms.local").orElse(engineer);
        User quality = userRepository.findByEmail("quality.engineer@pms.local").orElse(engineer);
        User tech = userRepository.findByEmail("technician@pms.local").orElseThrow();
        User burnInTech = userRepository.findByEmail("burnin.tech@pms.local").orElse(tech);

        replaceLegacySerial("R9SN0001", "FXGDL-R9-240521-001");
        replaceLegacySerial("R10SN0002", "FXGDL-R10-240521-002");

        ProductionServer s1 = createServer("PMS-001", "FXGDL-R9-240521-001", ServerModel.R9, ServerStatus.IN_TEST, "A01", "Cold Room GDL-01", engineer, tech);
        ProductionServer s2 = createServer("PMS-002", "FXGDL-R10-240521-002", ServerModel.R10, ServerStatus.FAILED, "A02", "Cold Room GDL-01", manufacturing, tech);
        ProductionServer s3 = createServer("PMS-003", "HPE-R9-240521-003", ServerModel.R9, ServerStatus.PASSED, "A03", "Cold Room GDL-01", quality, burnInTech);
        ProductionServer s4 = createServer("PMS-004", "DELL-R10-240521-004", ServerModel.R10, ServerStatus.RELEASED, "B01", "Cold Room GDL-02", manufacturing, burnInTech);
        ProductionServer s5 = createServer("PMS-005", "SRV-R9-BURNIN-005", ServerModel.R9, ServerStatus.RETEST, "B02", "Burn-In Chamber 02", engineer, burnInTech);
        ProductionServer s6 = createServer("PMS-006", "SRV-R10-DEBUG-006", ServerModel.R10, ServerStatus.DEBUG, "DBG-01", "Debug Bench GDL", quality, tech);

        TestCatalog memory = findTest("Memory");
        TestCatalog ethernet = findTest("Ethernet");
        TestCatalog bmc = findTest("BMC");
        TestCatalog storage = findTest("Storage");
        TestCatalog thermal = findTest("Burn-In");
        TestCatalog pdu = findTest("PDU");
        TestCatalog sel = findTest("SEL");

        createServerTest(s1, ethernet, TestStatus.PASSED, "1Gb/10Gb link stable, no packet loss detected.", tech, 8);
        createServerTest(s2, memory, TestStatus.FAILED, "RAM ECC correctable errors detected on DIMM B1.", tech, 6);
        createServerTest(s3, storage, TestStatus.PASSED, "NVMe health check passed and throughput is within expected range.", burnInTech, 5);
        createServerTest(s4, bmc, TestStatus.PASSED, "BMC Redfish endpoint responded and sensor inventory was collected.", burnInTech, 4);
        createServerTest(s5, thermal, TestStatus.RETEST, "Thermal throttling observed during burn-in at sustained CPU load.", burnInTech, 3);
        createServerTest(s6, pdu, TestStatus.FAILED, "PSU redundancy failure when load was switched to secondary PDU.", tech, 2);
        createServerTest(s6, sel, TestStatus.FAILED, "SEL log full or inaccessible during BMC event log review.", tech, 1);
        createServerTest(s1, bmc, TestStatus.PASSED, "Management controller responded over Redfish and IPMI.", tech, 30);
        createServerTest(s2, ethernet, TestStatus.FAILED, "Ethernet port 3 did not establish link during network validation.", tech, 54);
        createServerTest(s3, thermal, TestStatus.PASSED, "Burn-in completed with stable thermal margin.", burnInTech, 78);
        createServerTest(s4, storage, TestStatus.PASSED, "RAID and NVMe inventory matched expected BOM.", burnInTech, 102);
        createServerTest(s5, sel, TestStatus.RETEST, "SEL log review requires retest after BMC reset.", burnInTech, 126);

        createFailure(s2, memory, "RAM ECC correctable errors on DIMM B1 during memory stress.", Severity.HIGH, FailureStatus.OPEN, tech, "Reseat DIMM B1, run memory diagnostics, and repeat stress test.", 6);
        createFailure(s1, ethernet, "Ethernet port without link on NIC port 3 after OS installation.", Severity.MEDIUM, FailureStatus.IN_PROGRESS, tech, "Check switch port, replace DAC cable, and retest link negotiation.", 5);
        createFailure(s6, pdu, "PSU redundancy failure while switching load between PDU outlets.", Severity.CRITICAL, FailureStatus.OPEN, tech, "Validate PSU seating, confirm PDU outlet current, and run redundancy test again.", 2);
        createFailure(s6, bmc, "BMC not responding over network during Redfish health check.", Severity.HIGH, FailureStatus.RETEST_REQUIRED, tech, "Reset BMC, verify management VLAN, and update BMC firmware if needed.", 4);
        createFailure(s3, storage, "NVMe drive not detected in slot 2 during inventory validation.", Severity.MEDIUM, FailureStatus.CLOSED, burnInTech, "Reseated NVMe drive and confirmed detection after cold boot.", 24);
        createFailure(s5, thermal, "Thermal throttling during burn-in after 42 minutes of sustained load.", Severity.CRITICAL, FailureStatus.OPEN, burnInTech, "Inspect fan profile, verify heatsink torque, and repeat burn-in cycle.", 3);
        createFailure(s6, sel, "SEL log full or inaccessible from BMC event log interface.", Severity.LOW, FailureStatus.FIXED, tech, "Cleared SEL log and confirmed event log access through IPMI.", 1);
        updateLegacyFailureText();
    }

    /**
     * Crea registros iniciales para que la matriz de trazabilidad muestre datos reales al abrir la pantalla.
     */
    private void seedTraceabilityRecords() {
        User engineer = userRepository.findByEmail("engineer@pms.local").orElseThrow();
        User manufacturing = userRepository.findByEmail("manufacturing.engineer@pms.local").orElse(engineer);
        User quality = userRepository.findByEmail("quality.engineer@pms.local").orElse(engineer);
        User tech = userRepository.findByEmail("technician@pms.local").orElseThrow();
        User burnInTech = userRepository.findByEmail("burnin.tech@pms.local").orElse(tech);

        ProductionServer s1 = serverRepository.findBySerialNumber("FXGDL-R9-240521-001").orElseThrow();
        ProductionServer s2 = serverRepository.findBySerialNumber("FXGDL-R10-240521-002").orElseThrow();
        ProductionServer s3 = serverRepository.findBySerialNumber("HPE-R9-240521-003").orElseThrow();
        ProductionServer s4 = serverRepository.findBySerialNumber("DELL-R10-240521-004").orElseThrow();
        ProductionServer s5 = serverRepository.findBySerialNumber("SRV-R9-BURNIN-005").orElseThrow();
        ProductionServer s6 = serverRepository.findBySerialNumber("SRV-R10-DEBUG-006").orElseThrow();

        Pdu pduA01 = findPdu("PDU-GDL-CR1-A01");
        Pdu pduA02 = findPdu("PDU-GDL-CR1-A02");
        Pdu pduB01 = findPdu("PDU-GDL-CR2-B01");
        RaspberryDevice rpiOs = findRaspberry("RPI-GDL-A01-OSLOAD");
        RaspberryDevice rpiBurnIn = findRaspberry("RPI-GDL-A02-BURNIN");
        RaspberryDevice rpiDebug = findRaspberry("RPI-GDL-B01-DEBUG");

        TestCatalog bmc = findTest("BMC");
        TestCatalog memory = findTest("Memory");
        TestCatalog ethernet = findTest("Ethernet");
        TestCatalog thermal = findTest("burn-in");
        TestCatalog sel = findTest("SEL");
        TestCatalog pdu = findTest("PDU");

        createTraceability(s1, rpiOs, pduA01, "A01-03", "Cold Room GDL-01", "Rack A01 / Slot 03", ethernet, TestStatus.PASSED,
                "Ethernet validation passed with stable 10Gb link and no packet loss.", null, null,
                "Validated switch link, speed negotiation and traffic stability.", engineer, tech, 8);
        createTraceability(s2, rpiOs, pduA02, "A02-05", "Cold Room GDL-01", "Rack A02 / Slot 05", memory, TestStatus.FAILED,
                "Memory stress failed after ECC correctable errors were detected.", "RAM ECC correctable errors on DIMM B1", Severity.HIGH,
                "Reseat DIMM B1, run diagnostics and repeat memory stress.", manufacturing, tech, 6);
        createTraceability(s3, rpiBurnIn, pduA01, "A01-08", "Cold Room GDL-01", "Rack A03 / Slot 08", bmc, TestStatus.PASSED,
                "BMC responded over Redfish and IPMI. Sensor inventory collected successfully.", null, null,
                "Confirmed management network, sensor readings and remote power control.", quality, burnInTech, 5);
        createTraceability(s4, rpiBurnIn, pduB01, "B01-02", "Cold Room GDL-02", "Rack B01 / Slot 02", pdu, TestStatus.PASSED,
                "PSU and PDU redundancy checks passed during controlled load switch.", null, null,
                "Validated PSU status, redundancy and outlet telemetry.", manufacturing, burnInTech, 4);
        createTraceability(s5, rpiBurnIn, pduB01, "B01-06", "Burn-In Chamber 02", "Rack B02 / Burn-In Slot 06", thermal, TestStatus.RETEST,
                "Thermal margin degraded under sustained CPU load during burn-in.", "Thermal throttling during burn-in", Severity.CRITICAL,
                "Inspect heatsink torque, fan profile and repeat burn-in cycle.", engineer, burnInTech, 3);
        createTraceability(s6, rpiDebug, pduB01, "B01-09", "Debug Bench GDL", "Debug Bench DBG-01", bmc, TestStatus.FAILED,
                "BMC did not respond over management VLAN during Redfish health check.", "BMC not responding over network", Severity.HIGH,
                "Reset BMC, verify VLAN assignment and update firmware if required.", quality, tech, 2);
        createTraceability(s6, rpiDebug, pduB01, "B01-10", "Debug Bench GDL", "Debug Bench DBG-01", pdu, TestStatus.FAILED,
                "Server lost redundancy when load was switched to secondary PDU outlet.", "PSU redundancy failure", Severity.CRITICAL,
                "Validate PSU seating, inspect PDU outlet current and rerun redundancy test.", quality, tech, 1);
        createTraceability(s6, rpiDebug, pduB01, "B01-10", "Debug Bench GDL", "Debug Bench DBG-01", sel, TestStatus.FAILED,
                "SEL log could not be read from BMC event log interface.", "SEL log full or inaccessible", Severity.LOW,
                "Clear SEL log and confirm event log access through IPMI.", quality, tech, 1);
    }

    /**
     * Busca una prueba del catálogo usando una palabra clave del nombre.
     */
    private TestCatalog findTest(String keyword) {
        return testCatalogRepository.findAll().stream()
                .filter(test -> test.getName().toLowerCase().contains(keyword.toLowerCase()))
                .findFirst()
                .orElseThrow();
    }

    /**
     * Busca una PDU semilla por nombre para relacionarla con trazabilidad.
     */
    private Pdu findPdu(String name) {
        return pduRepository.findAll().stream()
                .filter(pdu -> pdu.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    /**
     * Busca una Raspberry semilla por nombre para relacionarla con trazabilidad.
     */
    private RaspberryDevice findRaspberry(String name) {
        return raspberryRepository.findAll().stream()
                .filter(device -> device.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    /**
     * Registra una línea de trazabilidad realista evitando duplicados al reiniciar la aplicación.
     */
    private void createTraceability(ProductionServer server, RaspberryDevice raspberry, Pdu pdu, String pduPort, String coldRoom,
                                    String physicalLocation, TestCatalog test, TestStatus status, String result, String failure,
                                    Severity severity, String correctiveAction, User engineer, User technician, int hoursAgo) {
        boolean alreadyExists = traceabilityRepository.findAll().stream()
                .anyMatch(record -> record.getServer().getSerialNumber().equals(server.getSerialNumber())
                        && record.getTestCatalog().getName().equals(test.getName())
                        && result.equals(record.getResult()));
        if (alreadyExists) return;

        TraceabilityRecord record = new TraceabilityRecord();
        record.setServer(server);
        record.setRaspberry(raspberry);
        record.setPdu(pdu);
        record.setPduPort(pduPort);
        record.setColdRoom(coldRoom);
        record.setPhysicalLocation(physicalLocation);
        record.setTestCatalog(test);
        record.setTestStatus(status);
        record.setResult(result);
        record.setDetectedFailure(failure);
        record.setSeverity(severity);
        record.setCorrectiveAction(correctiveAction);
        record.setResponsibleEngineer(engineer);
        record.setResponsibleTechnician(technician);
        record.setStartDate(LocalDateTime.now().minusHours(hoursAgo + 1));
        record.setEndDate(LocalDateTime.now().minusHours(hoursAgo));
        record.setComments("Traceability record generated for manufacturing and validation flow.");
        record.setEvidenceLogReference("logs/" + server.getSerialNumber() + "/" + test.getName().replace(" ", "_") + ".log");
        traceabilityRepository.save(record);
    }

    /**
     * Reemplaza seriales genéricos antiguos por seriales más cercanos a manufactura.
     */
    private void replaceLegacySerial(String legacySerial, String newSerial) {
        if (serverRepository.existsBySerialNumber(newSerial)) return;
        serverRepository.findBySerialNumber(legacySerial).ifPresent(server -> {
            server.setSerialNumber(newSerial);
            serverRepository.save(server);
        });
    }

    /**
     * Crea un servidor semilla solo cuando el serial no existe.
     */
    private ProductionServer createServer(String internalId, String serial, ServerModel model, ServerStatus status, String rack, String location, User engineer, User tech) {
        return serverRepository.findBySerialNumber(serial).map(existing -> {
            existing.setInternalId(internalId);
            existing.setModel(model);
            existing.setRackNumber(rack);
            existing.setLocation(location);
            existing.setStatus(status);
            existing.setResponsibleEngineer(engineer);
            existing.setAssignedTechnician(tech);
            existing.setObservations("Manufacturing validation unit assigned to server test flow.");
            return serverRepository.save(existing);
        }).orElseGet(() -> {
            ProductionServer server = server(internalId, serial, model, status, rack, location, engineer, tech);
            return serverRepository.save(server);
        });
    }

    /**
     * Registra un resultado de prueba realista evitando duplicados al reiniciar la aplicación.
     */
    private void createServerTest(ProductionServer server, TestCatalog catalog, TestStatus status, String result, User technician, int hoursAgo) {
        boolean alreadyExists = serverTestRepository.findAll().stream()
                .anyMatch(test -> test.getServer().getSerialNumber().equals(server.getSerialNumber())
                        && test.getTestCatalog().getName().equals(catalog.getName())
                        && result.equals(test.getResult()));
        if (alreadyExists) return;

        ServerTest serverTest = new ServerTest();
        serverTest.setServer(server);
        serverTest.setTestCatalog(catalog);
        serverTest.setStatus(status);
        serverTest.setTechnician(technician);
        serverTest.setStartedAt(LocalDateTime.now().minusHours(hoursAgo + 1));
        serverTest.setFinishedAt(LocalDateTime.now().minusHours(hoursAgo));
        serverTest.setResult(result);
        serverTest.setComments("Initial test record for the manufacturing dashboard.");
        serverTestRepository.save(serverTest);
    }

    /**
     * Registra una falla con acción correctiva y evidencia de log si no existe previamente.
     */
    private void createFailure(ProductionServer server, TestCatalog catalog, String description, Severity severity, FailureStatus status, User technician, String correctiveAction, int hoursAgo) {
        boolean alreadyExists = failureRepository.findAll().stream()
                .anyMatch(failure -> failure.getServer().getSerialNumber().equals(server.getSerialNumber()) && failure.getDescription().equals(description));
        if (alreadyExists) return;

        Failure failure = new Failure();
        failure.setServer(server);
        failure.setTestCatalog(catalog);
        failure.setDescription(description);
        failure.setSeverity(severity);
        failure.setStatus(status);
        failure.setAssignedTechnician(technician);
        failure.setDetectedAt(LocalDateTime.now().minusHours(hoursAgo));
        failure.setCorrectiveAction(correctiveAction);
        failure.setComments("Event detected during the server validation flow.");
        failure.setEvidenceLog("logs/" + server.getSerialNumber() + "/" + catalog.getName().replace(" ", "_") + ".log");
        if (status == FailureStatus.CLOSED || status == FailureStatus.FIXED) {
            failure.setClosedAt(LocalDateTime.now().minusHours(Math.max(0, hoursAgo - 1)));
        }
        failureRepository.save(failure);
    }

    /**
     * Construye la entidad base de servidor con datos de ubicación y responsables.
     */
    private ProductionServer server(String internalId, String serial, ServerModel model, ServerStatus status, String rack, String location, User engineer, User tech) {
        ProductionServer server = new ProductionServer();
        server.setInternalId(internalId);
        server.setSerialNumber(serial);
        server.setModel(model);
        server.setRackNumber(rack);
        server.setLocation(location);
        server.setStatus(status);
        server.setEntryDate(LocalDateTime.now().minusDays(1));
        server.setResponsibleEngineer(engineer);
        server.setAssignedTechnician(tech);
        server.setObservations("Manufacturing validation unit assigned to server test flow.");
        return server;
    }

    private void updateLegacyFailureText() {
        failureRepository.findAll().forEach(failure -> {
            if (failure.getDescription() != null && failure.getDescription().contains("ECC")) {
                failure.setDescription("RAM ECC correctable errors during memory stress.");
            }
            if (failure.getCorrectiveAction() != null && failure.getCorrectiveAction().contains("DIMM B1")) {
                failure.setCorrectiveAction("Inspect DIMM B1 and repeat the memory validation test.");
            }
            if (failure.getComments() != null && failure.getComments().contains("validaci")) {
                failure.setComments("Event detected during the server validation flow.");
            }
            failureRepository.save(failure);
        });
    }
}
