import { useEffect, useState } from 'react';
import api from '../api/api';
import StatusBadge from '../components/StatusBadge.jsx';

// Vista administrativa para consultar usuarios semilla y usuarios registrados.
export default function Users() {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => { api.get('/users').then((res) => setUsers(res.data)).catch(() => setError('Solo ADMIN puede consultar usuarios.')); }, []);

  return (
    <section className="page">
      <div className="page-title"><h1>Usuarios</h1><p>Roles y accesos del sistema.</p></div>
      {error && <div className="error">{error}</div>}
      <div className="panel table-panel">
        <table>
          <thead><tr><th>Nombre</th><th>Email</th><th>Rol</th><th>Activo</th></tr></thead>
          <tbody>{users.map((user) => <tr key={user.id}><td>{user.fullName}</td><td>{user.email}</td><td><StatusBadge value={user.role} /></td><td>{user.active ? 'Sí' : 'No'}</td></tr>)}</tbody>
        </table>
      </div>
    </section>
  );
}
