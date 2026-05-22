import { NavLink, Outlet } from 'react-router-dom';
import { Activity, AlertTriangle, ClipboardList, Cpu, Database, LayoutDashboard, LogOut, Server, Users } from 'lucide-react';
import { useAuth } from '../context/AuthContext.jsx';

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
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand"><Activity size={24} /> <span>PMS</span></div>
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
          <div>
            <strong>Production Monitoring System</strong>
            <span>{user?.fullName} · {user?.role}</span>
          </div>
          <button className="icon-button" onClick={logout} title="Cerrar sesión"><LogOut size={18} /></button>
        </header>
        <Outlet />
      </main>
    </div>
  );
}
