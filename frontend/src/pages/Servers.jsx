import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/api';
import StatusBadge from '../components/StatusBadge.jsx';
import { buildSearchIndex, matchesSearch } from '../utils/search.js';

// Construye el índice de búsqueda de servidores normalizando estados enum como READY_FOR_TEST.
function buildServerSearchIndex(server) {
  return buildSearchIndex([
    server.internalId,
    server.serialNumber,
    server.model,
    server.rackNumber,
    server.location,
    server.status,
    server.responsibleEngineer?.fullName,
    server.assignedTechnician?.fullName,
    server.observations,
    server.entryDate,
    server.updatedAt
  ]);
}

// Lista de servidores con búsqueda por serial, modelo, estado, ingeniero o técnico.
export default function Servers() {
  const [servers, setServers] = useState([]);
  const [query, setQuery] = useState('');

  useEffect(() => { api.get('/servers').then((res) => setServers(res.data)); }, []);

  const filtered = useMemo(() => servers.filter((server) => matchesSearch(buildServerSearchIndex(server), query)), [servers, query]);

  return (
    <section className="page">
      <div className="page-title row">
        <div><h1>Server Management</h1><p>Registration and lifecycle tracking for R9, R10 and additional validation units.</p></div>
        <Link className="primary-link" to="/servers/new">New Server</Link>
      </div>
      <input className="search" placeholder="Search by serial, model, status, engineer or technician..." value={query} onChange={(e) => setQuery(e.target.value)} />
      <div className="panel table-panel">
        <table>
          <thead><tr><th>Serial</th><th>Model</th><th>Rack</th><th>Location</th><th>Status</th><th>Owner</th></tr></thead>
          <tbody>
            {filtered.map((server) => (
              <tr key={server.id}>
                <td><Link to={`/servers/${server.id}`}>{server.serialNumber}</Link></td>
                <td>{server.model}</td>
                <td>{server.rackNumber}</td>
                <td>{server.location}</td>
                <td><StatusBadge value={server.status} /></td>
                <td>{server.responsibleEngineer?.fullName || 'Unassigned'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
