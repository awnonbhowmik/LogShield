// Server Component — fetches scan list at request time.
import { getScans } from '@/lib/api';
import Link from 'next/link';
import HistoryFilters from '@/components/HistoryFilters';

export default async function HistoryPage() {
  const scans = await getScans();

  return (
    <div className="mx-auto max-w-5xl px-4 py-12">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-2xl font-bold text-white">Scan History</h1>
          <p className="mt-1 text-sm text-gray-400">{scans.length} scan{scans.length !== 1 ? 's' : ''} total</p>
        </div>
        <Link
          href="/upload"
          className="rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-500 transition-colors"
        >
          New scan
        </Link>
      </div>

      {/* HistoryFilters is a Client Component that wraps HistoryTable */}
      <HistoryFilters scans={scans} />
    </div>
  );
}
