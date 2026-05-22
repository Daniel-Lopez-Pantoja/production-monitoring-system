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
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedDevices();
        seedTests();
        seedServersAndFailures();
    }

    private void seedUsers() {
        for (UserRole roleName : UserRole.values()) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                role.setDescription("Rol " + roleName.name() + " del sistema.");
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
                row("Instalación de sistema operativo", "Valida que el servidor pueda bootear e instalar imagen/base OS.", "No detecta discos/USB; falla de boot por BIOS/UEFI; imagen corrupta.", true),
                row("POST / Power On Self Test", "Valida motherboard, CPU, RAM, BIOS y periféricos.", "No da video/POST; error de memoria; reinicio en inicialización.", true),
                row("BMC / IPMI / Redfish", "Valida sensores, logs, energía e inventario remoto.", "BMC sin red; sensores erróneos; firmware corrupto.", true),
                row("BIOS / Firmware Check", "Valida versiones de BIOS, BMC, CPLD, NIC y RAID.", "Versión incorrecta; update fallido; incompatibilidad BIOS-BMC.", true),
                row("Prueba de memoria RAM", "Valida estabilidad de DIMMs bajo lectura/escritura.", "DIMM no detectado; errores ECC; thermal trip.", true),
                row("Prueba de CPU", "Valida carga, núcleos, frecuencia, temperatura y estabilidad.", "CPU no detectada; throttling; crash bajo estrés.", true),
                row("Prueba de almacenamiento", "Valida discos, RAID, SMART y velocidad.", "Disco no aparece; SMART con errores; baja velocidad.", true),
                row("Prueba de red Ethernet", "Valida link, velocidad, tráfico y pérdida de paquetes.", "Puerto sin link; velocidad incorrecta; tráfico inestable.", false),
                row("Prueba de LEDs", "Valida LEDs de power, UID, NIC, fan, PSU y fault.", "LED apagado; LED incorrecto; intermitencia por firmware.", false),
                row("Prueba de fuentes de poder / PDU", "Valida energía, voltaje y estado de PSU/PDU.", "PSU no detectada; redundancia fallida; apagado por carga.", true),
                row("Prueba de fans", "Valida RPM, PWM, redundancia y respuesta térmica.", "Fan no gira; RPM fuera de rango; fan al 100%.", true),
                row("Prueba térmica / cuarto frío / burn-in", "Valida estabilidad prolongada bajo carga y temperatura.", "Sobrecalentamiento; throttling; apagado térmico.", true),
                row("Stress Test General", "Valida CPU, RAM, disco y red trabajando juntos.", "Kernel panic; reinicio inesperado; error intermitente.", true),
                row("Prueba de inventario HW", "Valida conteo de CPUs, RAM, discos, NICs, PSUs, fans y seriales.", "Componente faltante; FRU incorrecto; no coincide BOM.", false),
                row("Prueba de puertos USB", "Valida puertos USB, especialmente para instalaciones R10.", "USB no bootea; desconexiones; velocidad incorrecta.", false),
                row("Logs SEL / Event Log", "Valida errores registrados por BMC/BIOS durante prueba.", "Logs críticos; falsos positivos; SEL lleno o inaccesible.", true)
        );
        tests.forEach(data -> {
            if (!testCatalogRepository.existsByName(data[0])) {
                TestCatalog test = new TestCatalog();
                test.setName(data[0]);
                test.setValidates(data[1]);
                test.setPossibleFailures(data[2]);
                test.setCritical(Boolean.parseBoolean(data[3]));
                testCatalogRepository.save(test);
            }
        });
    }

    private String[] row(String name, String validates, String failures, boolean critical) {
        return new String[]{name, validates, failures, String.valueOf(critical)};
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

        TestCatalog memory = findTest("memoria");
        TestCatalog ethernet = findTest("Ethernet");
        TestCatalog bmc = findTest("BMC");
        TestCatalog storage = findTest("almacenamiento");
        TestCatalog thermal = findTest("burn-in");
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
        return serverRepository.findBySerialNumber(serial).orElseGet(() -> {
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
        serverTest.setComments("Registro inicial de prueba para dashboard de manufactura.");
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
        failure.setComments("Evento detectado durante flujo de validación de servidor.");
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
}
