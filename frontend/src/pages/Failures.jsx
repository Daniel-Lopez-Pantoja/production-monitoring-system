import { useEffect, useMemo, useState } from 'react';
import api from '../api/api';
import StatusBadge from '../components/StatusBadge.jsx';

// Registro de fallas con filtros por serial, prueba, severidad, estado o técnico.
export default function Failures() {
  const [failures, setFailures] = useState([]);
  const [query, setQuery] = useState('');

  useEffect(() => { api.get('/failures').then((res) => setFailures(res.data)); }, []);
  const filtered = useMemo(() => failures.filter((failure) => JSON.stringify(failure).toLowerCase().includes(query.toLowerCase())), [failures, query]);

  return (
    <section className="page">
      <div className="page-title"><h1>Fallas</h1><p>Seguimiento de defectos, severidad y acciones correctivas.</p></div>
      <input className="search" placeholder="Buscar por serial, prueba, falla, técnico, severidad..." value={query} onChange={(e) => setQuery(e.target.value)} />
      <div className="panel table-panel">
        <table>
          <thead><tr><th>Serial</th><th>Prueba</th><th>Descripción</th><th>Severidad</th><th>Estado</th><th>Acción</th></tr></thead>
          <tbody>{filtered.map((failure) => (
            <tr key={failure.id}>
              <td>{failure.server?.serialNumber}</td>
              <td>{failure.testCatalog?.name}</td>
              <td>{failure.description}</td>
              <td><StatusBadge value={failure.severity} /></td>
              <td><StatusBadge value={failure.status} /></td>
              <td>{failure.correctiveAction}</td>
            </tr>
          ))}</tbody>
        </table>
      </div>
    </section>
  );
}
