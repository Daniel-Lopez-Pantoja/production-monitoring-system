import { useState } from 'react';
import { Navigate } from 'react-router-dom';
import { Activity, ClipboardCheck, Factory, LockKeyhole, Network, ShieldCheck } from 'lucide-react';
import { useAuth } from '../context/AuthContext.jsx';

const benefits = [
  ['Server lifecycle tracking', Activity],
  ['Failure and corrective action control', ShieldCheck],
  ['Traceability matrix', Network],
  ['Production monitoring dashboard', ClipboardCheck]
];

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
      setError('Invalid credentials or backend service unavailable.');
    }
  }

  if (isAuthenticated) return <Navigate to="/" replace />;

  return (
    <section className="login-page premium-login">
      <div className="login-shell">
        <aside className="login-info-panel">
          <span className="login-badge"><Factory size={15} /> Manufacturing Server Validation Platform</span>
          <div>
            <div className="login-brand">PMS</div>
            <h1>Production Monitoring System</h1>
            <p>Enterprise-grade server validation, manufacturing tracking, traceability and Test Engineering operations.</p>
          </div>
          <div className="benefit-grid">
            {benefits.map(([label, Icon]) => (
              <div className="benefit-item" key={label}>
                <Icon size={18} />
                <span>{label}</span>
              </div>
            ))}
          </div>
          <div className="demo-note">Demo environment for portfolio and technical evaluation.</div>
        </aside>

        <form className="login-panel enterprise-login-panel" onSubmit={handleSubmit}>
          <div className="form-heading">
            <LockKeyhole size={22} />
            <div>
              <h2>Sign in</h2>
              <span>Access the manufacturing dashboard</span>
            </div>
          </div>

          <label>Email<input value={email} onChange={(e) => setEmail(e.target.value)} /></label>
          <label>Password<input type="password" value={password} onChange={(e) => setPassword(e.target.value)} /></label>
          {error && <div className="error">{error}</div>}
          <button type="submit">Sign in</button>

          <div className="demo-credentials">
            <strong>Demo credentials</strong>
            <span>Email: admin@pms.local</span>
            <span>Password: admin123</span>
          </div>
        </form>
      </div>
    </section>
  );
}
