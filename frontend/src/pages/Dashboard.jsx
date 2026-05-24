import { useEffect, useMemo, useState } from 'react';
import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  LabelList,
  Legend,
  Line,
  LineChart,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from 'recharts';
import api from '../api/api';
import StatusBadge from '../components/StatusBadge.jsx';

const chartColors = ['#176b87', '#175cd3', '#067647', '#b86e00', '#b42318', '#7a271a', '#475467', '#6941c6', '#0e9384'];
const statusColors = {
  'IN TEST': '#175cd3',
  FAILED: '#b42318',
  RELEASED: '#067647',
  'PENDING OS': '#b86e00',
  DEBUG: '#6941c6',
  RETEST: '#f79009',
  PASSED: '#039855',
  'READY FOR TEST': '#0e9384',
  'OS INSTALLED': '#176b87',
  DEFAULT: '#475467'
};

// Formatea fechas operativas para tablas recientes sin saturar la interfaz.
function formatDate(value) {
  if (!value) return 'N/A';
  return new Intl.DateTimeFormat('en-US', {
    month: 'short',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(new Date(value));
}

// Convierte mapas del backend en arreglos compatibles con Recharts.
function toChartData(data) {
  return Object.entries(data || {}).map(([name, value]) => ({
    name: name.replaceAll('_', ' '),
    value: Number(value || 0)
  }));
}

// Ordena, filtra y colorea los estados para que el gráfico horizontal muestre solo datos operativos relevantes.
function toStatusChartData(data) {
  const preferredOrder = ['FAILED', 'RETEST', 'DEBUG', 'IN TEST', 'READY FOR TEST', 'PENDING OS', 'OS INSTALLED', 'PASSED', 'RELEASED'];
  return toChartData(data)
    .filter((item) => item.value > 0)
    .map((item) => ({ ...item, color: statusColors[item.name] || statusColors.DEFAULT }))
    .sort((a, b) => {
      const aIndex = preferredOrder.indexOf(a.name);
      const bIndex = preferredOrder.indexOf(b.name);
      if (aIndex === -1 && bIndex === -1) return a.name.localeCompare(b.name);
      if (aIndex === -1) return 1;
      if (bIndex === -1) return -1;
      return aIndex - bIndex;
    });
}

// Calcula el máximo del eje X con base en el valor real más alto para evitar escalas visualmente exageradas.
function getStatusAxisMax(items) {
  const maxValue = Math.max(0, ...items.map((item) => item.value));
  if (maxValue <= 1) return 1;
  return Math.ceil(maxValue * 1.15);
}

// Genera ticks enteros y compactos para que el eje X sea legible cuando los conteos son pequeños.
function getStatusAxisTicks(maxValue) {
  if (maxValue <= 1) return [0, 1];
  return Array.from({ length: maxValue + 1 }, (_, index) => index);
}

// Define el color visual de una métrica según su nivel de riesgo.
function metricTone(value, dangerLimit = 1) {
  return Number(value || 0) >= dangerLimit ? 'danger' : 'success';
}

// Tarjeta compacta para mostrar KPIs de operación en formato de sala de control.
function MiniMetric({ label, value, tone = 'neutral' }) {
  return (
    <div className={`ops-metric ops-${tone}`}>
      <span>{label}</span>
      <strong>{value ?? 0}</strong>
    </div>
  );
}

// Contenedor estándar para mantener consistencia entre todas las gráficas.
function ChartPanel({ title, subtitle, children, className = '' }) {
  return (
    <div className={`panel chart-panel ${className}`}>
      <div className="panel-header">
        <h2>{title}</h2>
        <span>{subtitle}</span>
      </div>
      {children}
    </div>
  );
}

// Tooltip personalizado para que las gráficas mantengan una lectura clara en ambos temas.
function ChartTooltip({ active, payload, label }) {
  if (!active || !payload?.length) return null;
  return (
    <div className="chart-tooltip">
      <strong>{label || payload[0].name}</strong>
      <span>{payload[0].value}</span>
    </div>
  );
}

// Dashboard operativo para monitoreo de manufactura y Test Engineering.
export default function Dashboard() {
  const [data, setData] = useState(null);

  useEffect(() => {
    api.get('/dashboard').then((res) => setData(res.data));
  }, []);

  const recentActivity = useMemo(() => (data?.latestServers || []).map((server) => ({
    id: server.id,
    serial: server.serialNumber,
    action: 'Server status updated',
    engineer: server.responsibleEngineer?.fullName || 'Unassigned',
    status: server.status,
    timestamp: server.updatedAt
  })), [data]);

  const serversByStatus = useMemo(() => toStatusChartData(data?.serversByStatus), [data]);
  const statusAxisMax = useMemo(() => getStatusAxisMax(serversByStatus), [serversByStatus]);
  const statusAxisTicks = useMemo(() => getStatusAxisTicks(statusAxisMax), [statusAxisMax]);
  const failuresBySeverity = useMemo(() => toChartData(data?.failuresBySeverity), [data]);
  const testsByResult = useMemo(() => toChartData(data?.testsByResult), [data]);
  const dailyThroughput = useMemo(() => Object.entries(data?.dailyTestThroughput || {}).map(([day, tests]) => ({ day, tests })), [data]);

  if (!data) return <div className="page">Loading operational dashboard...</div>;

  return (
    <section className="page dashboard-page">
      <div className="page-title dashboard-title">
        <div>
          <h1>Production Control Dashboard</h1>
          <p>Real-time operational view for server manufacturing, validation, traceability, and release readiness.</p>
        </div>
        <div className="dashboard-health">
          <span>Line Health</span>
          <strong>{data.openFailures > 0 ? 'Attention Required' : 'Stable'}</strong>
        </div>
      </div>

      <div className="dashboard-section">
        <div className="section-heading">
          <h2>Production Overview</h2>
          <span>Server lifecycle status</span>
        </div>
        <div className="ops-grid seven">
          <MiniMetric label="Total Servers" value={data.totalServers} tone="info" />
          <MiniMetric label="In Test" value={data.serversInTest} tone="info" />
          <MiniMetric label="Failed" value={data.failedServers} tone={metricTone(data.failedServers)} />
          <MiniMetric label="Released" value={data.releasedServers} tone="success" />
          <MiniMetric label="Pending OS" value={data.pendingOsServers} tone="warning" />
          <MiniMetric label="Debug" value={data.debugServers} tone="warning" />
          <MiniMetric label="Retest" value={data.retestServers} tone="warning" />
        </div>
      </div>

      <div className="dashboard-section split">
        <div>
          <div className="section-heading">
            <h2>Failure Metrics</h2>
            <span>Quality and corrective action workload</span>
          </div>
          <div className="ops-grid compact">
            <MiniMetric label="Critical Failures" value={data.criticalFailures} tone={metricTone(data.criticalFailures)} />
            <MiniMetric label="Open Failures" value={data.openFailures} tone={metricTone(data.openFailures)} />
            <MiniMetric label="Closed Failures" value={data.closedFailures} tone="success" />
            {Object.entries(data.failuresBySeverity || {}).map(([severity, total]) => (
              <MiniMetric key={severity} label={severity.replaceAll('_', ' ')} value={total} tone={severity === 'CRITICAL' ? 'danger' : 'neutral'} />
            ))}
          </div>
        </div>
        <div>
          <div className="section-heading">
            <h2>Test Metrics</h2>
            <span>Execution status across validation flow</span>
          </div>
          <div className="ops-grid compact">
            <MiniMetric label="Total Tests Executed" value={data.totalTestsExecuted} tone="info" />
            <MiniMetric label="Passed Tests" value={data.passedTests} tone="success" />
            <MiniMetric label="Failed Tests" value={data.failedTests} tone={metricTone(data.failedTests)} />
            <MiniMetric label="Pending Tests" value={data.pendingTests} tone="warning" />
            <MiniMetric label="Retest Tests" value={data.retestTests} tone="warning" />
          </div>
        </div>
      </div>

      <div className="dashboard-section">
        <div className="section-heading">
          <h2>Charts</h2>
          <span>Manufacturing flow and validation quality indicators</span>
        </div>
        <div className="charts-grid four">
          <ChartPanel title="Servers by Status" subtitle="Vertical bar chart" className="status-chart-panel">
            {serversByStatus.length > 0 ? (
              <ResponsiveContainer width="100%" height={262}>
                <BarChart data={serversByStatus} margin={{ top: 24, right: 10, left: -18, bottom: 36 }}>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} />
                  <XAxis dataKey="name" interval={0} tick={{ fontSize: 10, fontWeight: 700 }} angle={-20} textAnchor="end" height={50} />
                  <YAxis allowDecimals={false} domain={[0, statusAxisMax]} ticks={statusAxisTicks} tick={{ fontSize: 11 }} />
                  <Tooltip content={<ChartTooltip />} />
                  <Bar dataKey="value" radius={[8, 8, 0, 0]} barSize={24}>
                    <LabelList dataKey="value" position="top" className="bar-value-label" />
                    {serversByStatus.map((entry) => <Cell key={entry.name} fill={entry.color} />)}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <div className="empty-state compact-empty-state">
                <strong>No active server status data</strong>
                <span>Status distribution will appear when servers enter the validation flow.</span>
              </div>
            )}
          </ChartPanel>

          <ChartPanel title="Failures by Severity" subtitle="Bar chart">
            <ResponsiveContainer width="100%" height={240}>
              <BarChart data={failuresBySeverity}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="name" tick={{ fontSize: 11 }} />
                <YAxis allowDecimals={false} />
                <Tooltip content={<ChartTooltip />} />
                <Bar dataKey="value" radius={[6, 6, 0, 0]}>
                  {failuresBySeverity.map((entry, index) => <Cell key={entry.name} fill={chartColors[index % chartColors.length]} />)}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </ChartPanel>

          <ChartPanel title="Tests by Result" subtitle="Doughnut chart">
            <ResponsiveContainer width="100%" height={240}>
              <PieChart>
                <Pie data={testsByResult} dataKey="value" nameKey="name" innerRadius={52} outerRadius={88}>
                  {testsByResult.map((entry, index) => <Cell key={entry.name} fill={chartColors[index % chartColors.length]} />)}
                </Pie>
                <Tooltip content={<ChartTooltip />} />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </ChartPanel>

          <ChartPanel title="Daily Test Throughput" subtitle="Line chart">
            <ResponsiveContainer width="100%" height={240}>
              <LineChart data={dailyThroughput}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="day" />
                <YAxis allowDecimals={false} />
                <Tooltip content={<ChartTooltip />} />
                <Line type="monotone" dataKey="tests" stroke="#0e9384" strokeWidth={3} dot={{ r: 4 }} />
              </LineChart>
            </ResponsiveContainer>
          </ChartPanel>
        </div>
      </div>

      <div className="dashboard-tables">
        <div className="panel table-panel">
          <div className="panel-header table-header">
            <h2>Recent Activity</h2>
            <span>Latest server updates</span>
          </div>
          <table>
            <thead><tr><th>Serial</th><th>Action</th><th>Engineer</th><th>Status</th><th>Timestamp</th></tr></thead>
            <tbody>
              {recentActivity.map((item) => (
                <tr key={item.id}>
                  <td>{item.serial}</td>
                  <td>{item.action}</td>
                  <td>{item.engineer}</td>
                  <td><StatusBadge value={item.status} /></td>
                  <td>{formatDate(item.timestamp)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="panel table-panel">
          <div className="panel-header table-header">
            <h2>Recent Failures</h2>
            <span>Latest quality events</span>
          </div>
          <table>
            <thead><tr><th>Serial</th><th>Test</th><th>Failure</th><th>Severity</th><th>Engineer</th><th>Date</th></tr></thead>
            <tbody>
              {(data.latestFailures || []).map((failure) => (
                <tr key={failure.id}>
                  <td>{failure.server?.serialNumber}</td>
                  <td>{failure.testCatalog?.name}</td>
                  <td>{failure.description}</td>
                  <td><StatusBadge value={failure.severity} /></td>
                  <td>{failure.server?.responsibleEngineer?.fullName || 'Unassigned'}</td>
                  <td>{formatDate(failure.detectedAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </section>
  );
}
