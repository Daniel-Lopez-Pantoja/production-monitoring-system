import { useEffect, useMemo, useState } from 'react';
import api from '../api/api';
import StatusBadge from '../components/StatusBadge.jsx';
import { buildSearchIndex, matchesSearch, normalizeSearchText } from '../utils/search.js';

// Da formato legible a las fechas de trazabilidad sin ocupar demasiado espacio en la tabla.
function formatTraceDate(value) {
  if (!value) return 'N/A';
  return new Intl.DateTimeFormat('en-US', {
    month: 'short',
    day: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(new Date(value));
}

// Une los campos importantes de un registro para permitir búsqueda operacional por serial, rack, PDU, prueba, falla o responsable.
function buildSearchText(record) {
  return buildSearchIndex([
    record.server?.serialNumber,
    record.server?.model,
    record.server?.rackNumber,
    record.server?.location,
    record.coldRoom,
    record.physicalLocation,
    record.pdu?.name,
    record.pduPort,
    record.raspberry?.name,
    record.testCatalog?.name,
    record.testStatus,
    record.result,
    record.detectedFailure,
    record.severity,
    record.responsibleEngineer?.fullName,
    record.responsibleTechnician?.fullName,
    record.startDate,
    record.endDate,
    record.createdAt,
    record.updatedAt
  ]);
}

// Matriz de trazabilidad con carga, error, estado vacío y búsqueda por campos técnicos clave.
export default function Traceability() {
  const [records, setRecords] = useState([]);
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/traceability')
      .then((res) => setRecords(res.data))
      .catch(() => setError('Traceability data could not be loaded. Please verify the backend service.'))
      .finally(() => setLoading(false));
  }, []);

  const filtered = useMemo(() => {
    const normalizedQuery = normalizeSearchText(query);
    if (!normalizedQuery) return records;
    return records.filter((record) => matchesSearch(buildSearchText(record), normalizedQuery));
  }, [records, query]);

  return (
    <section className="page">
      <div className="page-title">
        <h1>Traceability Matrix</h1>
        <p>Complete server validation history with test status, physical location, power mapping, automation devices and failure evidence.</p>
      </div>

      <input
        className="search"
        placeholder="Search by serial, model, rack, location, PDU, Raspberry, test, status, result, failure, severity, engineer, technician or date..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
      />

      {loading && <div className="panel empty-state">Loading traceability records...</div>}
      {error && <div className="error">{error}</div>}

      {!loading && !error && (
        <div className="panel table-panel">
          <table>
            <thead>
              <tr>
                <th>Serial</th>
                <th>Model</th>
                <th>Rack</th>
                <th>Location</th>
                <th>Test</th>
                <th>Status</th>
                <th>Result</th>
                <th>Severity</th>
                <th>PDU</th>
                <th>Raspberry</th>
                <th>Engineer</th>
                <th>Technician</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((record) => (
                <tr key={record.id}>
                  <td>{record.server?.serialNumber || 'N/A'}</td>
                  <td>{record.server?.model || 'N/A'}</td>
                  <td>{record.server?.rackNumber || record.physicalLocation || 'N/A'}</td>
                  <td>{record.coldRoom || record.server?.location || 'N/A'}</td>
                  <td>{record.testCatalog?.name || 'N/A'}</td>
                  <td><StatusBadge value={record.testStatus} /></td>
                  <td>
                    <strong>{record.result || 'N/A'}</strong>
                    {record.detectedFailure && <span className="table-subtext">{record.detectedFailure}</span>}
                  </td>
                  <td>{record.severity ? <StatusBadge value={record.severity} /> : <span className="muted-text">N/A</span>}</td>
                  <td>{record.pdu?.name || 'N/A'} {record.pduPort ? <span className="table-subtext">{record.pduPort}</span> : null}</td>
                  <td>{record.raspberry?.name || 'N/A'}</td>
                  <td>{record.responsibleEngineer?.fullName || 'Unassigned'}</td>
                  <td>{record.responsibleTechnician?.fullName || 'Unassigned'}</td>
                  <td>{formatTraceDate(record.endDate || record.updatedAt || record.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>

          {filtered.length === 0 && (
            <div className="empty-state">
              <strong>No traceability records found.</strong>
              <span>Try adjusting the search criteria or verify that traceability seed data has been loaded.</span>
            </div>
          )}
        </div>
      )}
    </section>
  );
}
