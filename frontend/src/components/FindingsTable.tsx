// Server Component — receives data as props, pure display.
import type { FindingResponse } from '@/types/scan';
import SeverityBadge from './SeverityBadge';

const CATEGORY_LABELS: Record<string, string> = {
  EMAIL:       'Email',
  IP_ADDRESS:  'IP Address',
  API_KEY:     'API Key',
  JWT_TOKEN:   'JWT Token',
  CREDIT_CARD: 'Credit Card',
};

export default function FindingsTable({ findings }: { findings: FindingResponse[] }) {
  if (findings.length === 0) {
    return (
      <div className="rounded-xl border border-gray-800 bg-gray-900 px-6 py-10 text-center text-gray-500 text-sm">
        No sensitive data detected.
      </div>
    );
  }

  return (
    <div className="rounded-xl border border-gray-800 bg-gray-900 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-800">
              <Th>Type</Th>
              <Th>Line</Th>
              <Th>Matched Value</Th>
              <Th>Redacted As</Th>
              <Th>Severity</Th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-800">
            {findings.map((f) => (
              <tr key={f.id} className="hover:bg-gray-800/50 transition-colors">
                <td className="px-4 py-3 text-gray-300">{CATEGORY_LABELS[f.category] ?? f.category}</td>
                <td className="px-4 py-3 text-gray-400 tabular-nums">{f.lineNumber}</td>
                <td className="px-4 py-3 font-mono text-red-400 max-w-xs truncate" title={f.matchedValue}>
                  {f.matchedValue}
                </td>
                <td className="px-4 py-3 font-mono text-emerald-400">{f.redactedValue}</td>
                <td className="px-4 py-3">
                  <SeverityBadge severity={f.severity} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function Th({ children }: { children: React.ReactNode }) {
  return (
    <th className="px-4 py-3 text-left text-xs font-medium uppercase tracking-wide text-gray-500">
      {children}
    </th>
  );
}
