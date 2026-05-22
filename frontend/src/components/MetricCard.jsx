// Tarjeta compacta para mostrar métricas principales del dashboard.
export default function MetricCard({ label, value, tone = 'neutral' }) {
  return (
    <div className={`metric-card metric-${tone}`}>
      <span>{label}</span>
      <strong>{value ?? 0}</strong>
    </div>
  );
}
