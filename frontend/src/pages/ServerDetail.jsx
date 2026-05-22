import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import api from '../api/api';
import StatusBadge from '../components/StatusBadge.jsx';

// Vista de detalle para consultar la información completa de un servidor.
export default function ServerDetail() {
  const { id } = useParams();
  const [server, setServer] = useState(null);

  useEffect(() => { api.get(`/servers/${id}`).then((res) => setServer(res.data)); }, [id]);

  if (!server) return <section className="page">Loading server...</section>;

  return (
    <section className="page">
      <div className="page-title">
        <h1>{server.serialNumber}</h1>
        <p>{server.internalId} · {server.model} · {server.location}</p>
      </div>
      <div className="detail-grid">
        <div className="panel"><h2>Status</h2><StatusBadge value={server.status} /></div>
        <div className="panel"><h2>Rack</h2><strong>{server.rackNumber || 'N/A'}</strong></div>
        <div className="panel"><h2>Engineer</h2><strong>{server.responsibleEngineer?.fullName || 'Unassigned'}</strong></div>
        <div className="panel"><h2>Technician</h2><strong>{server.assignedTechnician?.fullName || 'Unassigned'}</strong></div>
      </div>
      <div className="panel">
        <h2>Notes</h2>
        <p>{server.observations || 'No notes available.'}</p>
      </div>
    </section>
  );
}
