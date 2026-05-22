import { useState } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

// Pantalla de inicio de sesión contra el endpoint /api/auth/login.
export default function Login() {
  const { login, isAuthenticated } = useAuth();
  const [email, setEmail] = useState('admin@pms.local');
  const [password, setPassword] = useState('admin123');
  const [error, setError] = useState('');

  async function handleSubmit(event) {
    event.preventDefault();
    try {
      await login(email, password);
    } catch {
      setError('Credenciales inválidas o backend no disponible.');
    }
  }

  if (isAuthenticated) return <Navigate to="/" replace />;

  return (
    <section className="login-page">
      <form className="login-panel" onSubmit={handleSubmit}>
        <h1>Production Monitoring System</h1>
        <p>Acceso para equipos de Test Engineering, manufactura y soporte.</p>
        <label>Correo<input value={email} onChange={(e) => setEmail(e.target.value)} /></label>
        <label>Contraseña<input type="password" value={password} onChange={(e) => setPassword(e.target.value)} /></label>
        {error && <div className="error">{error}</div>}
        <button type="submit">Ingresar</button>
      </form>
    </section>
  );
}
