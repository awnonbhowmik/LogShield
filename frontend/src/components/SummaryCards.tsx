// Server Component — receives data as props, pure display.
import type { ScanUploadResponse } from '@/types/scan';
import SeverityBadge from './SeverityBadge';
import { scoreToLevel } from '@/lib/severity';

const CATEGORY_LABELS: Record<string, string> = {
  EMAIL:                'Emails',
  IP_ADDRESS:           'IP Addresses',
  API_KEY:              'API Keys',
  JWT_TOKEN:            'JWT Tokens',
  CREDIT_CARD:          'Credit Cards',
  DB_CONNECTION_STRING: 'DB Connection Strings',
};

export default function SummaryCards({ scan }: { scan: ScanUploadResponse }) {
  const level = scoreToLevel(scan.severityScore);
  const categories = Object.entries(scan.findingsByType).filter(([, v]) => v && v.length > 0);

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      {/* Total findings */}
      <Card label="Total Findings">
        <span className="text-3xl font-bold text-white">{scan.totalFindings}</span>
      </Card>

      {/* Severity score */}
      <Card label="Severity Score">
        <div className="flex items-end gap-2">
          <span className="text-3xl font-bold text-white">{scan.severityScore ?? 0}</span>
          <span className="mb-1 text-gray-400 text-sm">/ 100</span>
        </div>
        {level && <div className="mt-1"><SeverityBadge severity={level} /></div>}
      </Card>

      {/* Categories hit */}
      <Card label="Categories Detected">
        <span className="text-3xl font-bold text-white">{categories.length}</span>
        <p className="text-xs text-gray-500 mt-1">
          {categories.map(([k]) => CATEGORY_LABELS[k] ?? k).join(', ') || '—'}
        </p>
      </Card>

      {/* Status */}
      <Card label="Scan Status">
        <span className={`text-sm font-semibold ${scan.status === 'COMPLETED' ? 'text-emerald-400' : scan.status === 'FAILED' ? 'text-red-400' : 'text-yellow-400'}`}>
          {scan.status}
        </span>
        <p className="text-xs text-gray-500 mt-1">{scan.filename}</p>
      </Card>
    </div>
  );
}

function Card({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="rounded-xl border border-gray-800 bg-gray-900 p-5">
      <p className="text-xs font-medium uppercase tracking-wide text-gray-500 mb-2">{label}</p>
      {children}
    </div>
  );
}
