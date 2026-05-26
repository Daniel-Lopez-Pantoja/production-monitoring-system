import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/api';
import { useAuth } from '../context/AuthContext.jsx';

const initial = { internalId: '', serialNumber: '', model: 'R9', rackNumber: '', location: 'Cold Room GDL-01', nicheNumber: 1, status: 'PENDING_OS', observations: '' };

// Creates servers with authorized cold room and niche information for the manufacturing flow.
export default function ServerForm() {
  const [form, setForm] = useState(initial);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { user } = useAuth();
  const isDemoUser = user?.role === 'DEMO_USER';

  function update(field, value) { setForm((current) => ({ ...current, [field]: value })); }

  async function save(event) {
    event.preventDefault();
    // Blocks DEMO_USER mutations before calling protected backend endpoints.
    if (isDemoUser) {
      setError('Demo user has read-only access.');
      return;
    }
    try {
      await api.post('/servers', { ...form, nicheNumber: Number(form.nicheNumber) });
      navigate('/servers');
    } catch (err) {
      setError(err.response?.data?.error || 'The server could not be saved.');
    }
  }

  return (
    <section className="page narrow">
      <h1>New Server</h1>
      {isDemoUser && <div className="error">Demo user has read-only access.</div>}
      <form className="form-grid" onSubmit={save}>
        <label>Internal ID<input value={form.internalId} onChange={(e) => update('internalId', e.target.value)} required /></label>
        <label>Serial Number<input value={form.serialNumber} onChange={(e) => update('serialNumber', e.target.value)} required /></label>
        <label>Model<select value={form.model} onChange={(e) => update('model', e.target.value)}><option>R9</option><option>R10</option><option>OTHER</option></select></label>
        <label>Status<select value={form.status} onChange={(e) => update('status', e.target.value)}><option>PENDING_OS</option><option>OS_INSTALLED</option><option>READY_FOR_TEST</option><option>IN_TEST</option><option>FAILED</option><option>DEBUG</option><option>RETEST</option><option>PASSED</option><option>RELEASED</option></select></label>
        <label>Rack<input value={form.rackNumber} onChange={(e) => update('rackNumber', e.target.value)} /></label>
        <label>Location<select value={form.location} onChange={(e) => update('location', e.target.value)}><option>Cold Room GDL-01</option><option>Cold Room GDL-02</option><option>Cold Room GDL-03</option></select></label>
        <label>Niche<input type="number" min="1" max="32" value={form.nicheNumber} onChange={(e) => update('nicheNumber', e.target.value)} required /></label>
        <label className="wide">Notes<textarea value={form.observations} onChange={(e) => update('observations', e.target.value)} /></label>
        {error && <div className="error wide">{error}</div>}
        <button className="wide" type="submit" disabled={isDemoUser}>Save Server</button>
      </form>
    </section>
  );
}
