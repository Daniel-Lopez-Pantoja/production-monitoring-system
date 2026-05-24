import { useEffect, useMemo, useState } from 'react';
import api from '../api/api';
import StatusBadge from '../components/StatusBadge.jsx';
import { buildSearchIndex, matchesSearch } from '../utils/search.js';

// Normaliza nombres de pruebas recibidos desde la API.
function displayTestName(name) {
  return name || 'N/A';
}

// Normaliza textos de fallas recibidos desde la API.
function displayFailureText(value) {
  return value || 'N/A';
}

// Construye el índice de búsqueda para que enums como IN_PROGRESS también coincidan con "in progress".
function buildFailureSearchIndex(failure) {
  return buildSearchIndex([
    failure.server?.serialNumber,
    failure.testCatalog?.name,
    failure.description,
    failure.severity,
    failure.status,
    failure.correctiveAction,
    failure.assignedTechnician?.fullName,
    failure.comments,
    failure.evidenceLog,
    failure.detectedAt,
    failure.closedAt
  ]);
}

// Registro de fallas con filtros por serial, prueba, severidad, estado o técnico.
export default function Failures() {
  const [failures, setFailures] = useState([]);
  const [query, setQuery] = useState('');

  useEffect(() => { api.get('/failures').then((res) => setFailures(res.data)); }, []);
  const filtered = useMemo(() => failures.filter((failure) => matchesSearch(buildFailureSearchIndex(failure), query)), [failures, query]);

  return (
    <section className="page">
      <div className="page-title"><h1>Failure Management</h1><p>Failure tracking, severity control and corrective action follow-up.</p></div>
      <input className="search" placeholder="Search by serial, test, failure, technician or severity..." value={query} onChange={(e) => setQuery(e.target.value)} />
      <div className="panel table-panel">
        <table>
          <thead><tr><th>Serial</th><th>Test</th><th>Description</th><th>Severity</th><th>Status</th><th>Corrective Action</th></tr></thead>
          <tbody>{filtered.map((failure) => (
            <tr key={failure.id}>
              <td>{failure.server?.serialNumber}</td>
              <td>{displayTestName(failure.testCatalog?.name)}</td>
              <td>{displayFailureText(failure.description)}</td>
              <td><StatusBadge value={failure.severity} /></td>
              <td><StatusBadge value={failure.status} /></td>
              <td>{displayFailureText(failure.correctiveAction)}</td>
            </tr>
          ))}</tbody>
        </table>
      </div>
    </section>
  );
}
