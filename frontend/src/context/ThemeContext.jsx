import { createContext, useContext, useEffect, useMemo, useState } from 'react';

const ThemeContext = createContext(null);

// Proveedor global que aplica el tema visual y lo conserva en localStorage.
export function ThemeProvider({ children }) {
  const [theme, setTheme] = useState(() => localStorage.getItem('pms_theme') || 'light');

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('pms_theme', theme);
  }, [theme]);

  // Alterna entre modo claro y oscuro para toda la aplicación.
  function toggleTheme() {
    setTheme((current) => current === 'dark' ? 'light' : 'dark');
  }

  const value = useMemo(() => ({ theme, toggleTheme, isDark: theme === 'dark' }), [theme]);
  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>;
}

// Hook para leer y cambiar el tema desde cualquier componente.
export function useTheme() {
  return useContext(ThemeContext);
}
