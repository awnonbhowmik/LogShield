// Server Component — fetches scan list at request time, reads search params from URL.
import { getScans, ApiError } from '@/lib/api';
import Link from 'next/link';
import HistoryFilters from '@/components/HistoryFilters';
import HistoryTable from '@/components/HistoryTable';
import PaginationControls from '@/components/PaginationControls';

interface Props {
  searchParams: Promise<{ search?: string; page?: string; size?: string }>;
}

export default async function HistoryPage({ searchParams }: Props) {
  const { search, page, size } = await searchParams;
  const currentPage = Math.max(0, Number(page ?? 0));
  const pageSize = Number(size ?? 20);

  let data;
  let fetchError: string | null = null;
  try {
    data = await getScans({ search, page: currentPage, size: pageSize });
  } catch (err) {
    fetchError = err instanceof ApiError ? err.message : 'Failed to load scan history.';
  }

  return (
    <div className="mx-auto max-w-5xl px-4 py-12">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-2xl font-bold text-white">Scan History</h1>
          {data && (
            <p className="mt-1 text-sm text-gray-400">
              {data.totalElements} scan{data.totalElements !== 1 ? 's' : ''} total
            </p>
          )}
        </div>
        <Link
          href="/upload"
          className="rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-500 transition-colors"
        >
          New scan
        </Link>
      </div>

      {fetchError ? (
        <div className="rounded-xl border border-red-800 bg-red-950/30 px-6 py-8 text-center text-red-400 text-sm">
          {fetchError}
        </div>
      ) : (
        <>
          <HistoryFilters initialSearch={search ?? ''} currentPage={currentPage} pageSize={pageSize} />
          <div className="mt-4">
            <HistoryTable scans={data!.content} />
          </div>
          {data!.totalPages > 1 && (
            <PaginationControls
              currentPage={currentPage}
              totalPages={data!.totalPages}
              search={search}
            />
          )}
        </>
      )}
    </div>
  );
}
