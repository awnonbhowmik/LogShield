// Server Component — receives data as props, pure display.
// Search/filter (Phase 6) will wrap this with a Client Component shell.
import Link from 'next/link';
import type { ScanSummaryResponse } from '@/types/scan';
import SeverityBadge from './SeverityBadge';
import type { FindingSeverity } from '@/types/scan';

function scoreToLevel(score: number | null): FindingSeverity | null {
  if (score === null || score === 0) return null;
  if (score <= 15) return 'LOW';
  if (score <= 40) return 'MEDIUM';
  if (score <= 75) return 'HIGH';
  return 'CRITICAL';
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  });
}

export default function HistoryTable({ scans }: { scans: ScanSummaryResponse[] }) {
  if (scans.length === 0) {
    return (
      <div className="rounded-xl border border-gray-800 bg-gray-900 px-6 py-16 text-center text-gray-500 text-sm">
        No scans yet.{' '}
        <Link href="/upload" className="text-blue-400 hover:underline">
          Upload a file
        </Link>{' '}
        to get started.
      </div>
    );
  }

  return (
    <div className="rounded-xl border border-gray-800 bg-gray-900 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-gray-800">
              <Th>Filename</Th>
              <Th>Scanned At</Th>
              <Th>Status</Th>
              <Th>Score</Th>
              <Th>Findings</Th>
              <Th>Actions</Th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-800">
            {scans.map((scan) => {
              const level = scoreToLevel(scan.severityScore);
              return (
                <tr key={scan.id} className="hover:bg-gray-800/50 transition-colors">
                  <td className="px-4 py-3 font-mono text-gray-200 max-w-xs truncate" title={scan.filename}>
                    {scan.filename}
                  </td>
                  <td className="px-4 py-3 text-gray-400 whitespace-nowrap">{formatDate(scan.uploadedAt)}</td>
                  <td className="px-4 py-3">
                    <StatusChip status={scan.status} />
                  </td>
                  <td className="px-4 py-3">
                    {level ? (
                      <div className="flex items-center gap-2">
                        <span className="tabular-nums text-gray-300">{scan.severityScore}</span>
                        <SeverityBadge severity={level} />
                      </div>
                    ) : (
                      <span className="text-gray-600">—</span>
                    )}
                  </td>
                  <td className="px-4 py-3 tabular-nums text-gray-300">{scan.findingCount}</td>
                  <td className="px-4 py-3">
                    <Link
                      href={`/scans/${scan.id}`}
                      className="text-xs text-blue-400 hover:text-blue-300 hover:underline"
                    >
                      View →
                    </Link>
                  </td>
                </tr>
              );
            })}
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

function StatusChip({ status }: { status: string }) {
  const styles =
    status === 'COMPLETED' ? 'bg-emerald-950 text-emerald-400 ring-emerald-800'
    : status === 'FAILED'   ? 'bg-red-950    text-red-400    ring-red-800'
    :                         'bg-yellow-950 text-yellow-400 ring-yellow-800';
  return (
    <span className={`inline-flex rounded-full px-2 py-0.5 text-xs font-medium ring-1 ring-inset ${styles}`}>
      {status}
    </span>
  );
}
