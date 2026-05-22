import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/api';
import StatusBadge from '../components/StatusBadge.jsx';

// Lista de servidores con búsqueda por serial, modelo, estado, ingeniero o técnico.
export default function Servers() {
  const [servers, setServers] = useState([]);
  const [query, setQuery] = useState('');

  useEffect(() => { api.get('/servers').then((res) => setServers(res.data)); }, []);

  const filtered = useMemo(() => servers.filter((server) => JSON.stringify(server).toLowerCase().includes(query.toLowerCase())), [servers, query]);

  return (
    <section className="page">
      <div className="page-title row">
        <div><h1>Servidores</h1><p>Registro y seguimiento de unidades R9, R10 y otros modelos.</p></div>
        <Link className="primary-link" to="/servers/new">Nuevo servidor</Link>
      </div>
      <input className="search" placeholder="Buscar por serial, modelo, estado, ingeniero, técnico..." value={query} onChange={(e) => setQuery(e.target.value)} />
      <div className="panel table-panel">
        <table>
          <thead><tr><th>Serial</th><th>Modelo</th><th>Rack</th><th>Ubicación</th><th>Estado</th><th>Responsable</th></tr></thead>
          <tbody>
            {filtered.map((server) => (
              <tr key={server.id}>
                <td><Link to={`/servers/${server.id}`}>{server.serialNumber}</Link></td>
                <td>{server.model}</td>
                <td>{server.rackNumber}</td>
                <td>{server.location}</td>
                <td><StatusBadge value={server.status} /></td>
                <td>{server.responsibleEngineer?.fullName || 'Sin asignar'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
