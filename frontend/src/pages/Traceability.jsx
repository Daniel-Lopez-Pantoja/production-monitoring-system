import { useEffect, useMemo, useState } from 'react';
import api from '../api/api';
import StatusBadge from '../components/StatusBadge.jsx';

// Matriz de trazabilidad con búsqueda global por serial, prueba, resultado, PDU o severidad.
export default function Traceability() {
  const [records, setRecords] = useState([]);
  const [query, setQuery] = useState('');

  useEffect(() => { api.get('/traceability').then((res) => setRecords(res.data)); }, []);

  const filtered = useMemo(() => records.filter((record) => JSON.stringify(record).toLowerCase().includes(query.toLowerCase())), [records, query]);

  return (
    <section className="page">
      <div className="page-title"><h1>Matriz de trazabilidad</h1><p>Historial completo de pruebas, fallas, evidencia y acciones.</p></div>
      <input className="search" placeholder="Buscar por serial, prueba, resultado, falla, PDU, Raspberry, severidad..." value={query} onChange={(e) => setQuery(e.target.value)} />
      <div className="panel table-panel">
        <table>
          <thead><tr><th>Serial</th><th>Prueba</th><th>Estado</th><th>Resultado</th><th>Severidad</th><th>PDU</th><th>Raspberry</th></tr></thead>
          <tbody>
            {filtered.map((record) => (
              <tr key={record.id}>
                <td>{record.server?.serialNumber}</td>
                <td>{record.testCatalog?.name}</td>
                <td><StatusBadge value={record.testStatus} /></td>
                <td>{record.result}</td>
                <td><StatusBadge value={record.severity} /></td>
                <td>{record.pdu?.name || 'N/A'} {record.pduPort || ''}</td>
                <td>{record.raspberry?.name || 'N/A'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
