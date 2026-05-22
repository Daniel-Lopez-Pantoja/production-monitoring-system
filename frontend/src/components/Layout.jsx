import { NavLink, Outlet } from 'react-router-dom';
import { Activity, AlertTriangle, ClipboardList, Cpu, Database, LayoutDashboard, LogOut, Moon, Server, Sun, Users } from 'lucide-react';
import { useAuth } from '../context/AuthContext.jsx';
import { useTheme } from '../context/ThemeContext.jsx';

const links = [
  ['/', 'Dashboard', LayoutDashboard],
  ['/servers', 'Servidores', Server],
  ['/traceability', 'Trazabilidad', ClipboardList],
  ['/tests', 'Pruebas', Cpu],
  ['/failures', 'Fallas', AlertTriangle],
  ['/reports', 'Reportes', Database],
  ['/users', 'Usuarios', Users]
];

// Layout principal con navegación lateral y encabezado de sesión.
export default function Layout() {
  const { user, logout } = useAuth();
  const { isDark, toggleTheme } = useTheme();
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark"><Activity size={22} /></div>
          <div>
            <strong>PMS</strong>
            <span>Production Monitoring System</span>
          </div>
        </div>
        <nav>
          {links.map(([to, label, Icon]) => (
            <NavLink key={to} to={to} end={to === '/'} className={({ isActive }) => isActive ? 'active nav-item' : 'nav-item'}>
              <Icon size={18} /> {label}
            </NavLink>
          ))}
        </nav>
      </aside>
      <main className="main">
        <header className="topbar">
          <div className="topbar-title">
            <strong>Production Monitoring System</strong>
            <span>Server Validation & Manufacturing Tracking</span>
          </div>
          <div className="topbar-actions">
            <span className="environment-badge">Manufacturing Dashboard</span>
            <span className="user-chip">{user?.fullName} · {user?.role}</span>
            <button className="icon-button" onClick={toggleTheme} title={isDark ? 'Activar modo claro' : 'Activar modo oscuro'}>
              {isDark ? <Sun size={18} /> : <Moon size={18} />}
            </button>
            <button className="icon-button" onClick={logout} title="Cerrar sesión"><LogOut size={18} /></button>
          </div>
        </header>
        <Outlet />
      </main>
    </div>
  );
}
