import { useEffect, useState } from 'react';
import api from '../api/api';

// Catálogo inicial de pruebas con validación y fallas posibles.
export default function TestCatalog() {
  const [tests, setTests] = useState([]);
  useEffect(() => { api.get('/tests').then((res) => setTests(res.data)); }, []);

  return (
    <section className="page">
      <div className="page-title"><h1>Catálogo de pruebas</h1><p>Pruebas estándar para servidores R9, R10 y otros modelos.</p></div>
      <div className="catalog-grid">
        {tests.map((test) => (
          <article className="panel test-card" key={test.id}>
            <h2>{test.name}</h2>
            <p><strong>Valida:</strong> {test.validates}</p>
            <p><strong>Fallas posibles:</strong> {test.possibleFailures}</p>
            <span className={test.critical ? 'badge badge-critical' : 'badge'}>{test.critical ? 'Crítica' : 'Regular'}</span>
          </article>
        ))}
      </div>
    </section>
  );
}
