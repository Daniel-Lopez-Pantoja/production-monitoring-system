import { useEffect, useState } from 'react';
import api from '../api/api';
import MetricCard from '../components/MetricCard.jsx';
import StatusBadge from '../components/StatusBadge.jsx';

// Dashboard con métricas de operación y últimos eventos registrados.
export default function Dashboard() {
  const [data, setData] = useState(null);

  useEffect(() => {
    api.get('/dashboard').then((res) => setData(res.data));
  }, []);

  if (!data) return <div className="page">Cargando dashboard...</div>;

  return (
    <section className="page">
      <div className="page-title">
        <h1>Dashboard</h1>
        <p>Monitoreo ejecutivo del flujo de servidores en prueba.</p>
      </div>
      <div className="metrics-grid">
        <MetricCard label="Total servidores" value={data.totalServers} />
        <MetricCard label="En prueba" value={data.serversInTest} tone="info" />
        <MetricCard label="Fallidos" value={data.failedServers} tone="danger" />
        <MetricCard label="Liberados" value={data.releasedServers} tone="success" />
        <MetricCard label="Fallas críticas" value={data.criticalFailures} tone="danger" />
        <MetricCard label="Pruebas pendientes" value={data.pendingTests} tone="warning" />
        <MetricCard label="Pruebas fallidas" value={data.failedTests} tone="danger" />
      </div>
      <div className="section-grid">
        <div className="panel">
          <h2>Servidores por estado</h2>
          <div className="status-list">
            {Object.entries(data.serversByStatus || {}).map(([status, total]) => <div key={status}><StatusBadge value={status} /><strong>{total}</strong></div>)}
          </div>
        </div>
        <div className="panel">
          <h2>Últimos servidores actualizados</h2>
          <table><tbody>{(data.latestServers || []).map((server) => <tr key={server.id}><td>{server.serialNumber}</td><td><StatusBadge value={server.status} /></td></tr>)}</tbody></table>
        </div>
      </div>
    </section>
  );
}
