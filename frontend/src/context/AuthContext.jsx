import { createContext, useContext, useMemo, useState } from 'react';
import api from '../api/api';

const AuthContext = createContext(null);

// Mantiene sesión, usuario y funciones de login/logout disponibles en toda la app.
export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem('pms_user') || 'null'));

  async function login(email, password) {
    const { data } = await api.post('/auth/login', { email, password });
    localStorage.setItem('pms_token', data.token);
    localStorage.setItem('pms_user', JSON.stringify(data));
    setUser(data);
  }

  function logout() {
    localStorage.removeItem('pms_token');
    localStorage.removeItem('pms_user');
    setUser(null);
  }

  const value = useMemo(() => ({ user, login, logout, isAuthenticated: Boolean(user) }), [user]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
