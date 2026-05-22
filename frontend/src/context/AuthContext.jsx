import { createContext, useContext, useMemo, useState } from 'react';
import api from '../api/api';

const AuthContext = createContext(null);

// Mantiene sesión, usuario y funciones de login/logout disponibles en toda la app.
export function AuthProvider({ children }) {
  const [user, setUser] = useState(() =>
    JSON.parse(localStorage.getItem('pms_user') || 'null')
  );

  async function login(email, password) {
    const { data } = await api.post('/auth/login', {
      email,
      password,
    });

    /**
     * El backend devuelve únicamente el JWT como string.
     * Si en el futuro devuelve un objeto, también será compatible.
     */
    const token =
      typeof data === 'string'
        ? data
        : data.token;

    localStorage.setItem('pms_token', token);

    // Guardamos información básica del usuario autenticado.
    const userData = {
      email,
    };

    localStorage.setItem('pms_user', JSON.stringify(userData));
    setUser(userData);
  }

  function logout() {
    localStorage.removeItem('pms_token');
    localStorage.removeItem('pms_user');
    setUser(null);
  }

  const value = useMemo(
    () => ({
      user,
      login,
      logout,
      isAuthenticated: Boolean(user),
    }),
    [user]
  );

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}