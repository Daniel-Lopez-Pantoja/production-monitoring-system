import { useEffect, useMemo, useState } from 'react';
import { Activity, ChevronDown, ChevronRight, Cpu, HardDrive, MemoryStick, Network, Power, Search, Thermometer, Wrench } from 'lucide-react';
import api from '../api/api';

const filters = ['All', 'Critical', 'Standard', 'Hardware', 'Firmware', 'Network', 'Thermal', 'Power', 'Storage'];

const testTranslations = {
  'Instalación de sistema operativo': {
    name: 'Operating System Installation',
    validates: 'Validates that the server can boot and install the base OS image using the expected deployment path.',
    failures: 'Disk or USB not detected, BIOS/UEFI boot misconfiguration, corrupted image or incomplete installation.'
  },
  'POST / Power On Self Test': {
    name: 'POST / Power On Self Test',
    validates: 'Validates that motherboard, CPU, RAM, BIOS and required peripherals initialize correctly.',
    failures: 'No video or POST, memory error codes, initialization reboot or platform startup failure.'
  },
  'BMC / IPMI / Redfish': {
    name: 'BMC / IPMI / Redfish',
    validates: 'Validates remote management access, sensor readings, logs, power control and hardware inventory.',
    failures: 'BMC not responding over network, incorrect sensor values, corrupted or incompatible BMC firmware.'
  },
  'BIOS / Firmware Check': {
    name: 'BIOS / Firmware Check',
    validates: 'Validates the required BIOS, BMC, CPLD, NIC and RAID firmware versions for the server model.',
    failures: 'Incorrect firmware baseline, failed update process, BIOS-BMC compatibility issue.'
  },
  'Prueba de memoria RAM': {
    name: 'Memory RAM Test',
    validates: 'Validates DIMM stability under intensive read/write workload and ECC monitoring.',
    failures: 'DIMM not detected, ECC correctable errors, uncorrectable error or thermal trip.'
  },
  'Prueba de CPU': {
    name: 'CPU Test',
    validates: 'Validates CPU load, cores, frequency, temperature and stability under stress.',
    failures: 'CPU not detected, thermal throttling or crash during stress workload.'
  },
  'Prueba de almacenamiento': {
    name: 'Storage Test',
    validates: 'Validates SSD, HDD, NVMe, RAID, SMART status, read/write stability and throughput.',
    failures: 'Drive not detected, SMART errors, low performance due to slot, cable or backplane issue.'
  },
  'Prueba de red Ethernet': {
    name: 'Ethernet Network Test',
    validates: 'Validates link status, negotiated speed, traffic stability, packet loss and throughput.',
    failures: 'Ethernet port without link, incorrect negotiated speed, packet loss or unstable traffic.'
  },
  'Prueba de LEDs': {
    name: 'LED Validation Test',
    validates: 'Validates power, UID, NIC, fan, PSU, fault and status LEDs against expected platform behavior.',
    failures: 'LED not turning on, wrong LED state, inverted or intermittent signal due to firmware or GPIO issue.'
  },
  'Prueba de fuentes de poder / PDU': {
    name: 'Power Supply / PDU Test',
    validates: 'Validates PSU power input, voltage reporting, redundancy and PDU outlet mapping.',
    failures: 'PSU not detected, PSU redundancy failure, server shutdown during load or PDU transition.'
  },
  'Prueba de fans': {
    name: 'Fan Test',
    validates: 'Validates fan RPM, PWM control, redundancy and thermal response behavior.',
    failures: 'Fan not spinning, RPM out of range, fan locked at full speed due to sensor or thermal profile issue.'
  },
  'Prueba térmica / cuarto frío / burn-in': {
    name: 'Thermal / Cold Room / Burn-In Test',
    validates: 'Validates long-duration stability under load and thermal control before production release.',
    failures: 'Overheating, thermal throttling or protective thermal shutdown.'
  },
  'Stress Test General': {
    name: 'General Stress Test',
    validates: 'Validates CPU, RAM, disk and network running simultaneously under combined workload.',
    failures: 'Kernel panic, unexpected reboot or intermittent hard-to-reproduce failure.'
  },
  'Prueba de inventario HW': {
    name: 'Hardware Inventory Test',
    validates: 'Validates expected CPUs, RAM, drives, NICs, PSUs, fans and serial/FRU information.',
    failures: 'Missing component, incorrect serial or FRU data, configuration mismatch against BOM.'
  },
  'Prueba de puertos USB': {
    name: 'USB Port Test',
    validates: 'Validates USB boot and stability, especially for R10 operating system installation paths.',
    failures: 'USB does not boot, intermittent disconnects or incorrect USB speed.'
  },
  'Logs SEL / Event Log': {
    name: 'SEL / Event Log Review',
    validates: 'Validates BMC/BIOS event logs collected during the test cycle.',
    failures: 'Critical log events, false positives, SEL log full or inaccessible event log interface.'
  }
};

// Traduce los datos visibles del catálogo sin modificar el contrato del backend.
function getDisplayTest(test) {
  const translation = testTranslations[test.name];
  return {
    name: translation?.name || test.name,
    validates: translation?.validates || test.validates,
    possibleFailures: translation?.failures || test.possibleFailures
  };
}

// Enriquece cada prueba con categoría, icono, componentes y acción recomendada para mejorar la experiencia visual.
function getTestMetadata(test) {
  const display = getDisplayTest(test);
  const name = `${test.name} ${display.name}`.toLowerCase();
  if (name.includes('bmc') || name.includes('bios') || name.includes('sel') || name.includes('firmware')) {
    return { category: 'Firmware', Icon: Activity, components: 'BMC, BIOS, CPLD, Redfish, IPMI, event logs', action: 'Verify firmware baseline, management network access, sensor readings and event log availability.' };
  }
  if (name.includes('red ethernet') || name.includes('network')) {
    return { category: 'Network', Icon: Network, components: 'NIC ports, switch links, DAC cables, VLANs, throughput path', action: 'Check link negotiation, cable seating, switch configuration and packet loss under traffic.' };
  }
  if (name.includes('térmica') || name.includes('thermal') || name.includes('burn-in') || name.includes('fans')) {
    return { category: 'Thermal', Icon: Thermometer, components: 'Fans, heatsinks, thermal sensors, airflow path, cold room', action: 'Review fan profile, heatsink torque, temperature sensors and repeat sustained load validation.' };
  }
  if (name.includes('pdu') || name.includes('fuentes') || name.includes('power')) {
    return { category: 'Power', Icon: Power, components: 'PSUs, PDU outlets, power cables, redundancy path', action: 'Validate PSU seating, PDU telemetry, outlet mapping and redundancy failover behavior.' };
  }
  if (name.includes('almacenamiento') || name.includes('storage') || name.includes('nvme') || name.includes('discos')) {
    return { category: 'Storage', Icon: HardDrive, components: 'NVMe, SSD, HDD, RAID, backplane, cables, SMART data', action: 'Confirm device detection, SMART health, slot mapping, RAID state and read/write throughput.' };
  }
  if (name.includes('memoria') || name.includes('ram')) {
    return { category: 'Hardware', Icon: MemoryStick, components: 'DIMMs, memory channels, CPU memory controller, ECC reporting', action: 'Reseat DIMMs, inspect population rules, run stress diagnostics and review ECC events.' };
  }
  if (name.includes('cpu') || name.includes('post') || name.includes('inventario') || name.includes('usb') || name.includes('led')) {
    return { category: 'Hardware', Icon: Cpu, components: 'Motherboard, CPU, RAM, USB ports, LEDs, FRU inventory', action: 'Validate hardware inventory, boot behavior, component detection and physical indicators.' };
  }
  return { category: 'Hardware', Icon: Wrench, components: 'Server platform, test fixture, validation tooling', action: 'Review test logs, confirm setup conditions and repeat validation after corrective action.' };
}

// Genera un resumen corto para mostrar las cards colapsadas sin perder contexto técnico.
function shortSummary(text) {
  if (!text) return 'Validation criteria pending documentation.';
  return text.length > 118 ? `${text.slice(0, 118)}...` : text;
}

// Página moderna del catálogo de pruebas con filtros, búsqueda y detalles expandibles.
export default function TestCatalog() {
  const [tests, setTests] = useState([]);
  const [query, setQuery] = useState('');
  const [activeFilter, setActiveFilter] = useState('All');
  const [expanded, setExpanded] = useState({});

  useEffect(() => { api.get('/tests').then((res) => setTests(res.data)); }, []);

  const enrichedTests = useMemo(() => tests.map((test) => ({ ...test, display: getDisplayTest(test), meta: getTestMetadata(test) })), [tests]);

  const filteredTests = useMemo(() => {
    const normalizedQuery = query.trim().toLowerCase();
    return enrichedTests.filter((test) => {
      const badge = test.critical ? 'critical' : 'standard';
      const searchable = [test.display.name, test.display.validates, test.display.possibleFailures, test.meta.category, test.meta.components, badge].join(' ').toLowerCase();
      const matchesQuery = !normalizedQuery || searchable.includes(normalizedQuery);
      const matchesFilter = activeFilter === 'All'
        || (activeFilter === 'Critical' && test.critical)
        || (activeFilter === 'Standard' && !test.critical)
        || test.meta.category === activeFilter;
      return matchesQuery && matchesFilter;
    });
  }, [activeFilter, enrichedTests, query]);

  // Alterna la vista expandida de una prueba individual.
  function toggleExpanded(id) {
    setExpanded((current) => ({ ...current, [id]: !current[id] }));
  }

  return (
    <section className="page catalog-page">
      <div className="page-title catalog-title">
        <div>
          <h1>Test Catalog</h1>
          <p>Standard validation tests for R9/R10 server manufacturing.</p>
        </div>
        <div className="catalog-count">{filteredTests.length} tests visible</div>
      </div>

      <div className="catalog-toolbar">
        <div className="search-with-icon">
          <Search size={18} />
          <input
            placeholder="Search by test name, component, possible failure or criticality..."
            value={query}
            onChange={(event) => setQuery(event.target.value)}
          />
        </div>
        <div className="quick-filters">
          {filters.map((filter) => (
            <button key={filter} className={activeFilter === filter ? 'filter-chip active' : 'filter-chip'} onClick={() => setActiveFilter(filter)}>
              {filter}
            </button>
          ))}
        </div>
      </div>

      <div className="catalog-grid compact-catalog">
        {filteredTests.map((test) => {
          const { Icon } = test.meta;
          const isExpanded = Boolean(expanded[test.id]);
          return (
            <article className="panel test-card modern-test-card" key={test.id}>
              <div className="test-card-header">
                <div className="test-icon"><Icon size={20} /></div>
                <div>
                  <h2>{test.display.name}</h2>
                  <span>{test.meta.category}</span>
                </div>
                <span className={test.critical ? 'badge badge-critical' : 'badge'}>{test.critical ? 'Critical' : 'Standard'}</span>
              </div>
              <p>{shortSummary(test.display.validates)}</p>
              <button className="ghost-button" onClick={() => toggleExpanded(test.id)}>
                {isExpanded ? <ChevronDown size={16} /> : <ChevronRight size={16} />}
                {isExpanded ? 'Hide details' : 'View details'}
              </button>
              {isExpanded && (
                <div className="test-details">
                  <div><strong>What it validates</strong><span>{test.display.validates}</span></div>
                  <div><strong>Possible failures</strong><span>{test.display.possibleFailures}</span></div>
                  <div><strong>Recommended action</strong><span>{test.meta.action}</span></div>
                  <div><strong>Related components</strong><span>{test.meta.components}</span></div>
                </div>
              )}
            </article>
          );
        })}
      </div>

      {filteredTests.length === 0 && (
        <div className="panel empty-state">
          <strong>No tests found.</strong>
          <span>Adjust the search or select another validation category.</span>
        </div>
      )}
    </section>
  );
}
