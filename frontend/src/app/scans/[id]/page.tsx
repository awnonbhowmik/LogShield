// Server Component — async, fetches scan detail at request time.
// params is a Promise in Next.js 16 — must be awaited.
import { notFound } from 'next/navigation';
import Link from 'next/link';
import { getScan, downloadUrl, ApiError } from '@/lib/api';
import FindingsTable from '@/components/FindingsTable';
import RedactedPreview from '@/components/RedactedPreview';
import SeverityBadge from '@/components/SeverityBadge';
import type { FindingSeverity } from '@/types/scan';

function scoreToLevel(score: number | null): FindingSeverity | null {
  if (!score) return null;
  if (score <= 15) return 'LOW';
  if (score <= 40) return 'MEDIUM';
  if (score <= 75) return 'HIGH';
  return 'CRITICAL';
}

function fmtDate(iso: string) {
  return new Date(iso).toLocaleString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  });
}

function fmtBytes(n: number) {
  if (n < 1024) return `${n} B`;
  if (n < 1024 * 1024) return `${(n / 1024).toFixed(1)} KB`;
  return `${(n / (1024 * 1024)).toFixed(1)} MB`;
}

export default async function ScanDetailPage({ params }: PageProps<'/scans/[id]'>) {
  const { id } = await params;

  let scan;
  try {
    scan = await getScan(Number(id));
  } catch (err) {
    if (err instanceof ApiError && err.status === 404) notFound();
    throw err; // let error.tsx handle unexpected failures
  }

  const level = scoreToLevel(scan.severityScore);

  return (
    <div className="mx-auto max-w-5xl px-4 py-12 space-y-8">
      {/* Back */}
      <Link href="/history" className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-300 transition-colors">
        ← Back to history
      </Link>

      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white font-mono break-all">{scan.filename}</h1>
          <p className="mt-1 text-sm text-gray-500">Scanned {fmtDate(scan.uploadedAt)}</p>
        </div>
        {scan.status === 'COMPLETED' && (
          <a
            href={downloadUrl(scan.id)}
            download
            className="inline-flex items-center gap-2 rounded-xl border border-gray-700 px-4 py-2 text-sm font-medium text-gray-300 hover:border-gray-500 hover:text-white transition-colors whitespace-nowrap"
          >
            <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            </svg>
            Download redacted file
          </a>
        )}
      </div>

      {/* Stat strip */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <Stat label="Status">
          <span className={
            scan.status === 'COMPLETED' ? 'text-emerald-400 font-semibold' :
            scan.status === 'FAILED'    ? 'text-red-400 font-semibold' :
                                          'text-yellow-400 font-semibold'
          }>
            {scan.status}
          </span>
        </Stat>
        <Stat label="Findings">
          <span className="text-white font-bold text-xl">{scan.findings.length}</span>
        </Stat>
        <Stat label="Severity">
          <div className="flex items-center gap-2">
            <span className="text-white font-bold text-xl">{scan.severityScore ?? 0}</span>
            {level && <SeverityBadge severity={level} />}
          </div>
        </Stat>
        <Stat label="File Size">
          <span className="text-white">{fmtBytes(scan.originalSize)}</span>
        </Stat>
      </div>

      {/* Findings */}
      <section>
        <h2 className="text-sm font-medium uppercase tracking-wide text-gray-500 mb-3">
          Findings ({scan.findings.length})
        </h2>
        <FindingsTable findings={scan.findings} />
      </section>

      {/* Redacted content */}
      <section>
        <h2 className="text-sm font-medium uppercase tracking-wide text-gray-500 mb-3">
          Redacted Content
        </h2>
        <RedactedPreview content={scan.redactedContent} />
      </section>
    </div>
  );
}

function Stat({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="rounded-xl border border-gray-800 bg-gray-900 p-4">
      <p className="text-xs font-medium uppercase tracking-wide text-gray-500 mb-1">{label}</p>
      {children}
    </div>
  );
}
