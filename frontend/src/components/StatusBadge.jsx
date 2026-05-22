// Convierte estados técnicos en badges visuales consistentes para tablas y detalle.
export default function StatusBadge({ value }) {
  const normalized = String(value || 'UNKNOWN').toLowerCase().replaceAll('_', '-');
  return <span className={`badge badge-${normalized}`}>{String(value || 'N/A').replaceAll('_', ' ')}</span>;
}
