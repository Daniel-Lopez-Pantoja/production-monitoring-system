import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/api';
import StatusBadge from '../components/StatusBadge.jsx';
import { useAuth } from '../context/AuthContext.jsx';
import { buildSearchIndex, matchesSearch } from '../utils/search.js';

// Builds the server search index and normalizes enum-like values such as READY_FOR_TEST.
function buildServerSearchIndex(server) {
  return buildSearchIndex([
    server.internalId,
    server.serialNumber,
    server.model,
    server.rackNumber,
    server.location,
    server.nicheNumber,
    server.status,
    server.currentRunningTest,
    server.estimatedRemainingTime,
    server.responsibleEngineer?.fullName,
    server.assignedTechnician?.fullName,
    server.observations,
    server.entryDate,
    server.updatedAt
  ]);
}

// Lists servers with manufacturing progress, location, niche, and operational search.
export default function Servers() {
  const [servers, setServers] = useState([]);
  const [query, setQuery] = useState('');
  const { user } = useAuth();
  const isDemoUser = user?.role === 'DEMO_USER';

  useEffect(() => { api.get('/servers').then((res) => setServers(res.data)); }, []);

  const filtered = useMemo(() => servers.filter((server) => matchesSearch(buildServerSearchIndex(server), query)), [servers, query]);

  return (
    <section className="page">
      <div className="page-title row">
        <div><h1>Server Management</h1><p>Registration and lifecycle tracking for R9, R10 and additional validation units.</p></div>
        {!isDemoUser && <Link className="primary-link" to="/servers/new">New Server</Link>}
      </div>
      <input className="search" placeholder="Search by serial, model, status, current test, location, niche, engineer or technician..." value={query} onChange={(e) => setQuery(e.target.value)} />
      <div className="panel table-panel">
        <table>
          <thead><tr><th>Serial</th><th>Model</th><th>Rack</th><th>Location</th><th>Niche</th><th>Status</th><th>Progress</th><th>Current Test</th><th>Remaining</th><th>Owner</th></tr></thead>
          <tbody>
            {filtered.map((server) => (
              <tr key={server.id}>
                <td><Link to={`/servers/${server.id}`}>{server.serialNumber}</Link></td>
                <td>{server.model}</td>
                <td>{server.rackNumber}</td>
                <td>{server.location}</td>
                <td>{server.nicheNumber || 'N/A'}</td>
                <td><StatusBadge value={server.status} /></td>
                <td>
                  <div className="mini-progress">
                    <span style={{ width: `${server.serverProgressPercentage || 0}%` }} />
                  </div>
                  <strong>{server.serverProgressPercentage || 0}%</strong>
                </td>
                <td>{server.currentRunningTest || 'Pending Start'}</td>
                <td>{server.estimatedRemainingTime || '0h 0m'}</td>
                <td>{server.responsibleEngineer?.fullName || 'Unassigned'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
