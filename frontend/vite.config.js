import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Configuración de Vite para ejecutar React en desarrollo y generar build de producción.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173
  }
});
