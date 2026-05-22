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
        createUser("Admin PMS", "admin@pms.local", "admin123", UserRole.ADMIN);
        createUser("Engineer Demo", "engineer@pms.local", "engineer123", UserRole.ENGINEER);
        createUser("Technician Demo", "technician@pms.local", "tech123", UserRole.TECHNICIAN);
        createUser("Operator Demo", "operator@pms.local", "operator123", UserRole.OPERATOR);
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
        if (!pduRepository.existsByName("PDU-RACK-A01")) {
            Pdu pdu = new Pdu();
            pdu.setName("PDU-RACK-A01");
            pdu.setIpAddress("10.10.20.11");
            pdu.setRack("A01");
            pdu.setLocation("Cold Room 1");
            pduRepository.save(pdu);
        }
        if (!raspberryRepository.existsByName("RPI-A01-CTRL")) {
            RaspberryDevice raspberry = new RaspberryDevice();
            raspberry.setName("RPI-A01-CTRL");
            raspberry.setIpAddress("10.10.30.21");
            raspberry.setRack("A01");
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
        if (serverRepository.count() > 0) return;
        User engineer = userRepository.findByEmail("engineer@pms.local").orElseThrow();
        User tech = userRepository.findByEmail("technician@pms.local").orElseThrow();
        ProductionServer r9 = server("PMS-001", "R9SN0001", ServerModel.R9, ServerStatus.IN_TEST, engineer, tech);
        ProductionServer r10 = server("PMS-002", "R10SN0002", ServerModel.R10, ServerStatus.FAILED, engineer, tech);
        serverRepository.saveAll(List.of(r9, r10));

        TestCatalog memory = testCatalogRepository.findAll().stream().filter(t -> t.getName().contains("memoria")).findFirst().orElseThrow();
        ServerTest serverTest = new ServerTest();
        serverTest.setServer(r10);
        serverTest.setTestCatalog(memory);
        serverTest.setStatus(TestStatus.FAILED);
        serverTest.setTechnician(tech);
        serverTest.setStartedAt(LocalDateTime.now().minusHours(3));
        serverTest.setFinishedAt(LocalDateTime.now().minusHours(2));
        serverTest.setResult("ECC errors detected on DIMM B1");
        serverTestRepository.save(serverTest);

        Failure failure = new Failure();
        failure.setServer(r10);
        failure.setTestCatalog(memory);
        failure.setDescription("Errores ECC correctables durante stress de memoria.");
        failure.setSeverity(Severity.HIGH);
        failure.setStatus(FailureStatus.OPEN);
        failure.setAssignedTechnician(tech);
        failure.setDetectedAt(LocalDateTime.now().minusHours(2));
        failure.setCorrectiveAction("Revisar DIMM B1 y repetir prueba de memoria.");
        failureRepository.save(failure);
    }

    private ProductionServer server(String internalId, String serial, ServerModel model, ServerStatus status, User engineer, User tech) {
        ProductionServer server = new ProductionServer();
        server.setInternalId(internalId);
        server.setSerialNumber(serial);
        server.setModel(model);
        server.setRackNumber("A01");
        server.setLocation("Cold Room 1");
        server.setStatus(status);
        server.setEntryDate(LocalDateTime.now().minusDays(1));
        server.setResponsibleEngineer(engineer);
        server.setAssignedTechnician(tech);
        server.setObservations("Servidor de ejemplo para pruebas del sistema.");
        return server;
    }
}
