import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import api from '../api/api';
import StatusBadge from '../components/StatusBadge.jsx';

// Formats an estimated duration range for manufacturing tests.
function formatDuration(test) {
  return `${test.estimatedMinMinutes || 0}-${test.estimatedMaxMinutes || 0} min`;
}

// Shows detailed diagnostics when a manufacturing test fails.
function FailureDiagnostics({ test }) {
  if (!test || test.status !== 'FAILED') return null;
  return (
    <div className="failure-diagnostics">
      <h3>Failure Diagnostics</h3>
      <strong>Failed at: {test.name}</strong>
      <div>
        <span>Possible Causes</span>
        <ul>{(test.possibleFailures || []).map((item) => <li key={item}>{item}</li>)}</ul>
      </div>
      <div>
        <span>Recommended Actions</span>
        <ul>{(test.recommendedActions || []).map((item) => <li key={item}>{item}</li>)}</ul>
      </div>
    </div>
  );
}

// Displays the complete manufacturing sequence with status, duration, and diagnostics.
function ManufacturingTimeline({ tests }) {
  return (
    <div className="panel">
      <div className="panel-header">
        <h2>Manufacturing Timeline</h2>
        <span>{tests.length} validation steps</span>
      </div>
      <div className="timeline">
        {tests.map((test) => (
          <div className={`timeline-item timeline-${String(test.status).toLowerCase().replaceAll('_', '-')}`} key={test.id}>
            <div className="timeline-marker">{test.sequenceOrder}</div>
            <div className="timeline-content">
              <div className="timeline-row">
                <strong>{test.name}</strong>
                <StatusBadge value={test.status} />
              </div>
              <p>{test.description}</p>
              <div className="timeline-meta">
                <span>Estimated Duration: {formatDuration(test)}</span>
                {test.failureCategory && <span>Failure Category: {test.failureCategory}</span>}
              </div>
              {test.status === 'FAILED' && <FailureDiagnostics test={test} />}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

// Server detail page with lifecycle information and manufacturing pipeline progress.
export default function ServerDetail() {
  const { id } = useParams();
  const [server, setServer] = useState(null);

  useEffect(() => { api.get(`/servers/${id}`).then((res) => setServer(res.data)); }, [id]);

  if (!server) return <section className="page">Loading server...</section>;

  return (
    <section className="page">
      <div className="page-title">
        <h1>{server.serialNumber}</h1>
        <p>{server.internalId} · {server.model} · {server.location} · Niche {server.nicheNumber || 'N/A'}</p>
      </div>
      <div className="detail-grid">
        <div className="panel"><h2>Status</h2><StatusBadge value={server.status} /></div>
        <div className="panel"><h2>Rack</h2><strong>{server.rackNumber || 'N/A'}</strong></div>
        <div className="panel"><h2>Niche</h2><strong>{server.nicheNumber || 'N/A'}</strong></div>
        <div className="panel"><h2>Progress</h2><strong>{server.serverProgressPercentage || 0}%</strong></div>
      </div>
      <div className="panel progress-panel">
        <div className="panel-header">
          <h2>Manufacturing Progress</h2>
          <span>Estimated Remaining Time: {server.estimatedRemainingTime || '0h 0m'}</span>
        </div>
        <div className="progress-bar large">
          <span style={{ width: `${server.serverProgressPercentage || 0}%` }} />
        </div>
        <div className="progress-summary">
          <strong>Current Test: {server.currentRunningTest || 'Pending Start'}</strong>
          <span>{server.estimatedRemainingMinutes || 0} minutes remaining</span>
        </div>
      </div>
      <div className="detail-grid">
        <div className="panel"><h2>Engineer</h2><strong>{server.responsibleEngineer?.fullName || 'Unassigned'}</strong></div>
        <div className="panel"><h2>Technician</h2><strong>{server.assignedTechnician?.fullName || 'Unassigned'}</strong></div>
      </div>
      <ManufacturingTimeline tests={server.manufacturingTests || []} />
      <div className="panel">
        <h2>Notes</h2>
        <p>{server.observations || 'No notes available.'}</p>
      </div>
    </section>
  );
}
