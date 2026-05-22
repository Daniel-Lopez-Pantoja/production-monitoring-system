import { useEffect, useState } from 'react';
import api from '../api/api';

// Reportes agregados con opción simple de exportación CSV desde el navegador.
export default function Reports() {
  const [reports, setReports] = useState({});

  useEffect(() => {
    Promise.all([
      api.get('/reports/servers-by-status'),
      api.get('/reports/failures-by-test'),
      api.get('/reports/failures-by-model')
    ]).then(([status, byTest, byModel]) => setReports({ status: status.data, byTest: byTest.data, byModel: byModel.data }));
  }, []);

  function exportCsv(name, data) {
    const csv = Object.entries(data || {}).map(([key, value]) => `${key},${value}`).join('\n');
    const url = URL.createObjectURL(new Blob([`Concepto,Total\n${csv}`], { type: 'text/csv' }));
    const link = document.createElement('a');
    link.href = url;
    link.download = `${name}.csv`;
    link.click();
  }

  return (
    <section className="page">
      <div className="page-title"><h1>Reportes</h1><p>Resumen operativo listo para exportar a CSV.</p></div>
      {Object.entries(reports).map(([name, data]) => (
        <div className="panel report-panel" key={name}>
          <div className="row"><h2>{name}</h2><button onClick={() => exportCsv(name, data)}>Exportar CSV</button></div>
          <table><tbody>{Object.entries(data || {}).map(([key, value]) => <tr key={key}><td>{key}</td><td>{value}</td></tr>)}</tbody></table>
        </div>
      ))}
    </section>
  );
}
