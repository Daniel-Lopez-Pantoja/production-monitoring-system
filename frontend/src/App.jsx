import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from './context/AuthContext.jsx';
import Layout from './components/Layout.jsx';
import Login from './pages/Login.jsx';
import Dashboard from './pages/Dashboard.jsx';
import Servers from './pages/Servers.jsx';
import ServerForm from './pages/ServerForm.jsx';
import ServerDetail from './pages/ServerDetail.jsx';
import Traceability from './pages/Traceability.jsx';
import TestCatalog from './pages/TestCatalog.jsx';
import Failures from './pages/Failures.jsx';
import Reports from './pages/Reports.jsx';
import Users from './pages/Users.jsx';

// Protege rutas para que solo usuarios autenticados puedan entrar al sistema.
function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? children : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<ProtectedRoute><Layout /></ProtectedRoute>}>
        <Route index element={<Dashboard />} />
        <Route path="servers" element={<Servers />} />
        <Route path="servers/new" element={<ServerForm />} />
        <Route path="servers/:id" element={<ServerDetail />} />
        <Route path="traceability" element={<Traceability />} />
        <Route path="tests" element={<TestCatalog />} />
        <Route path="failures" element={<Failures />} />
        <Route path="reports" element={<Reports />} />
        <Route path="users" element={<Users />} />
      </Route>
    </Routes>
  );
}
