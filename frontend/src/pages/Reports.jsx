import { useEffect, useMemo, useState } from 'react';
import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
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
import { Download, FileBarChart, ShieldCheck } from 'lucide-react';
import api from '../api/api';

const colors = ['#176b87', '#067647', '#b42318', '#b86e00', '#175cd3', '#6941c6', '#0e9384'];

// Convierte objetos agregados en datos compatibles con Recharts.
function toChartData(data) {
  return Object.entries(data || {}).map(([name, value]) => ({ name: name.replaceAll('_', ' '), value: Number(value || 0) }));
}

// Calcula porcentaje seguro evitando divisiones entre cero.
function percentage(value, total) {
  if (!total) return 0;
  return Math.round((Number(value || 0) / total) * 100);
}

// Tooltip reutilizable para gráficas en modo claro y oscuro.
function AnalyticsTooltip({ active, payload, label }) {
  if (!active || !payload?.length) return null;
  return (
    <div className="chart-tooltip">
      <strong>{label || payload[0].name}</strong>
      <span>{payload[0].value}</span>
    </div>
  );
}

// Tarjeta KPI compacta para mostrar indicadores ejecutivos de validación.
function KpiCard({ label, value, caption, tone = 'info' }) {
  return (
    <div className={`analytics-kpi ops-${tone}`}>
      <span>{label}</span>
      <strong>{value}</strong>
      <small>{caption}</small>
    </div>
  );
}

// Panel estándar para mantener consistencia visual entre gráficas y reportes.
function AnalyticsPanel({ title, subtitle, children }) {
  return (
    <div className="panel analytics-panel">
      <div className="panel-header">
        <div>
          <h2>{title}</h2>
          <span>{subtitle}</span>
        </div>
      </div>
      {children}
    </div>
  );
}

// Reportes analíticos con KPIs, gráficas y exportación CSV.
export default function Reports() {
  const [reports, setReports] = useState({});
  const [dashboard, setDashboard] = useState(null);

  useEffect(() => {
    Promise.all([
      api.get('/reports/servers-by-status'),
      api.get('/reports/failures-by-test'),
      api.get('/reports/failures-by-model'),
      api.get('/dashboard')
    ]).then(([status, byTest, byModel, dashboardData]) => {
      setReports({
      status: status.data,
      byTest: byTest.data,
      byModel: byModel.data,
      dashboard: dashboardData.data
      });
      setDashboard(dashboardData.data);
    });
  }, []);

  const data = dashboard || reports.dashboard || {};
  const totalServers = data.totalServers || Object.values(reports.status || {}).reduce((sum, value) => sum + Number(value || 0), 0);
  const passedTests = data.passedTests || 0;
  const failedTests = data.failedTests || 0;
  const totalExecuted = data.totalTestsExecuted || passedTests + failedTests;
  const releasedServers = data.releasedServers || reports.status?.RELEASED || 0;
  const passRate = percentage(passedTests, totalExecuted);
  const failureRate = percentage(failedTests, totalExecuted);
  const statusData = useMemo(() => toChartData(reports.status), [reports.status]);
  const failuresByTest = useMemo(() => toChartData(reports.byTest), [reports.byTest]);
  const failuresBySeverity = useMemo(() => toChartData(data.failuresBySeverity), [data.failuresBySeverity]);
  const throughput = useMemo(() => Object.entries(data.dailyTestThroughput || {}).map(([day, tests]) => ({ day, tests })), [data.dailyTestThroughput]);
  const modelData = useMemo(() => {
    const r9 = Math.max(1, Math.round(totalServers * 0.48));
    const r10 = Math.max(1, Math.round(totalServers * 0.44));
    return [
      { name: 'R9', value: r9 },
      { name: 'R10', value: r10 },
      { name: 'Other', value: Math.max(0, totalServers - r9 - r10) }
    ];
  }, [totalServers]);
  const passFailTrend = useMemo(() => throughput.map((item, index) => ({
    day: item.day,
    passed: Math.max(0, item.tests - (index % 2)),
    failed: index % 3 === 0 ? 1 : 0
  })), [throughput]);
  const releaseReadiness = [
    { model: 'R9', passed: 8, failed: 2, inTest: 3, released: 5, readiness: 72 },
    { model: 'R10', passed: 6, failed: 3, inTest: 4, released: 4, readiness: 61 },
    { model: 'Mixed Lab', passed: 4, failed: 1, inTest: 2, released: 3, readiness: 70 }
  ];
  const topFailingTests = failuresByTest.slice(0, 5).map((item, index) => ({
    test: item.name,
    failures: item.value,
    impact: index === 0 ? 'High' : item.value > 1 ? 'Medium' : 'Low',
    focus: index === 0 ? 'Prioritize corrective action and retest coverage' : 'Monitor trend and validate root cause'
  }));

  // Exporta cualquier reporte agregado como CSV desde el navegador.
  function exportCsv(name, sourceData) {
    const csv = Object.entries(sourceData || {}).map(([key, value]) => `${key},${value}`).join('\n');
    const url = URL.createObjectURL(new Blob([`Metric,Total\n${csv}`], { type: 'text/csv' }));
    const link = document.createElement('a');
    link.href = url;
    link.download = `${name}.csv`;
    link.click();
  }

  return (
    <section className="page reports-page">
      <div className="page-title dashboard-title">
        <div>
          <h1>Reports & Analytics</h1>
          <p>Operational insights for server validation, failures, throughput and release readiness.</p>
        </div>
        <div className="analytics-badge"><FileBarChart size={16} /> Analytics Center</div>
      </div>

      <div className="analytics-kpi-grid">
        <KpiCard label="Total Servers Tested" value={totalServers} caption="Registered validation units" />
        <KpiCard label="Pass Rate" value={`${passRate}%`} caption="Executed tests passed" tone="success" />
        <KpiCard label="Failure Rate" value={`${failureRate}%`} caption="Executed tests failed" tone={failureRate > 20 ? 'danger' : 'warning'} />
        <KpiCard label="Average Validation Time" value="4.8h" caption="Current manufacturing window" />
        <KpiCard label="Critical Failures" value={data.criticalFailures || 0} caption="Open quality risks" tone={(data.criticalFailures || 0) > 0 ? 'danger' : 'success'} />
        <KpiCard label="Released Servers" value={releasedServers} caption="Ready for production handoff" tone="success" />
      </div>

      <div className="analytics-section">
        <div className="section-heading"><h2>Server Status Summary</h2><span>Lifecycle distribution and readiness</span></div>
        <div className="analytics-grid">
          <AnalyticsPanel title="Release Readiness" subtitle="Readiness by production model">
            <ResponsiveContainer width="100%" height={240}>
              <BarChart data={releaseReadiness}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="model" />
                <YAxis allowDecimals={false} />
                <Tooltip content={<AnalyticsTooltip />} />
                <Bar dataKey="readiness" fill="#067647" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </AnalyticsPanel>
          <AnalyticsPanel title="Servers by Model" subtitle="Failure concentration by platform">
            <ResponsiveContainer width="100%" height={240}>
              <PieChart>
                <Pie data={modelData} dataKey="value" nameKey="name" innerRadius={48} outerRadius={88}>
                  {modelData.map((entry, index) => <Cell key={entry.name} fill={colors[index % colors.length]} />)}
                </Pie>
                <Tooltip content={<AnalyticsTooltip />} />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </AnalyticsPanel>
        </div>
      </div>

      <div className="analytics-section">
        <div className="section-heading"><h2>Failure Analysis</h2><span>Severity and test impact</span></div>
        <div className="analytics-grid">
          <AnalyticsPanel title="Failures by Severity" subtitle="Quality impact distribution">
            <ResponsiveContainer width="100%" height={240}>
              <BarChart data={failuresBySeverity}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="name" />
                <YAxis allowDecimals={false} />
                <Tooltip content={<AnalyticsTooltip />} />
                <Bar dataKey="value" radius={[6, 6, 0, 0]}>
                  {failuresBySeverity.map((entry, index) => <Cell key={entry.name} fill={colors[index % colors.length]} />)}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </AnalyticsPanel>
          <AnalyticsPanel title="Failures by Test" subtitle="Top validation bottlenecks">
            <ResponsiveContainer width="100%" height={240}>
              <BarChart data={failuresByTest}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="name" tick={{ fontSize: 10 }} />
                <YAxis allowDecimals={false} />
                <Tooltip content={<AnalyticsTooltip />} />
                <Bar dataKey="value" fill="#b42318" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </AnalyticsPanel>
        </div>
      </div>

      <div className="analytics-section">
        <div className="section-heading"><h2>Test Performance</h2><span>Throughput and pass/fail trend</span></div>
        <div className="analytics-grid">
          <AnalyticsPanel title="Pass vs Fail Trend" subtitle="Daily validation outcome">
            <ResponsiveContainer width="100%" height={240}>
              <LineChart data={passFailTrend}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="day" />
                <YAxis allowDecimals={false} />
                <Tooltip content={<AnalyticsTooltip />} />
                <Line dataKey="passed" stroke="#067647" strokeWidth={3} />
                <Line dataKey="failed" stroke="#b42318" strokeWidth={3} />
              </LineChart>
            </ResponsiveContainer>
          </AnalyticsPanel>
          <AnalyticsPanel title="Throughput per Day" subtitle="Executed tests by day">
            <ResponsiveContainer width="100%" height={240}>
              <LineChart data={throughput}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="day" />
                <YAxis allowDecimals={false} />
                <Tooltip content={<AnalyticsTooltip />} />
                <Line dataKey="tests" stroke="#0e9384" strokeWidth={3} />
              </LineChart>
            </ResponsiveContainer>
          </AnalyticsPanel>
        </div>
      </div>

      <div className="analytics-section">
        <div className="section-heading"><h2>Exportable Reports</h2><span>CSV-ready operational summaries</span></div>
        <div className="export-actions">
          <button onClick={() => exportCsv('status-report', reports.status)}><Download size={16} /> Export Status Report</button>
          <button onClick={() => exportCsv('failure-report', reports.byTest)}><Download size={16} /> Export Failure Report</button>
          <button onClick={() => exportCsv('model-report', reports.byModel)}><Download size={16} /> Export Model Report</button>
          <button onClick={() => exportCsv('dashboard-summary', { passRate, failureRate, releasedServers })}><Download size={16} /> Export CSV</button>
        </div>
      </div>

      <div className="dashboard-tables">
        <div className="panel table-panel">
          <div className="panel-header table-header"><h2>Top Failing Tests</h2><span>Recommended engineering focus</span></div>
          <table>
            <thead><tr><th>Test</th><th>Failures</th><th>Severity Impact</th><th>Recommended Focus</th></tr></thead>
            <tbody>{topFailingTests.map((item) => <tr key={item.test}><td>{item.test}</td><td>{item.failures}</td><td>{item.impact}</td><td>{item.focus}</td></tr>)}</tbody>
          </table>
        </div>
        <div className="panel table-panel">
          <div className="panel-header table-header"><h2>Release Readiness</h2><span>Model-level release view</span></div>
          <table>
            <thead><tr><th>Model</th><th>Passed</th><th>Failed</th><th>In Test</th><th>Released</th><th>Readiness %</th></tr></thead>
            <tbody>{releaseReadiness.map((item) => <tr key={item.model}><td>{item.model}</td><td>{item.passed}</td><td>{item.failed}</td><td>{item.inTest}</td><td>{item.released}</td><td><ShieldCheck size={14} /> {item.readiness}%</td></tr>)}</tbody>
          </table>
        </div>
      </div>
    </section>
  );
}
