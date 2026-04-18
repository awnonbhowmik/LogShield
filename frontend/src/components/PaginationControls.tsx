import Link from 'next/link';

interface Props {
  currentPage: number;
  totalPages: number;
  search?: string;
}

function pageUrl(page: number, search?: string) {
  const qs = new URLSearchParams();
  if (search) qs.set('search', search);
  if (page > 0) qs.set('page', String(page));
  return `/history${qs.toString() ? `?${qs}` : ''}`;
}

export default function PaginationControls({ currentPage, totalPages, search }: Props) {
  const hasPrev = currentPage > 0;
  const hasNext = currentPage < totalPages - 1;

  return (
    <div className="mt-6 flex items-center justify-between text-sm text-gray-400">
      <span>
        Page {currentPage + 1} of {totalPages}
      </span>
      <div className="flex gap-2">
        {hasPrev ? (
          <Link
            href={pageUrl(currentPage - 1, search)}
            className="rounded-xl border border-gray-700 px-4 py-2 hover:border-gray-500 hover:text-white transition-colors"
          >
            ← Previous
          </Link>
        ) : (
          <span className="rounded-xl border border-gray-800 px-4 py-2 text-gray-700 cursor-not-allowed">← Previous</span>
        )}

        {hasNext ? (
          <Link
            href={pageUrl(currentPage + 1, search)}
            className="rounded-xl border border-gray-700 px-4 py-2 hover:border-gray-500 hover:text-white transition-colors"
          >
            Next →
          </Link>
        ) : (
          <span className="rounded-xl border border-gray-800 px-4 py-2 text-gray-700 cursor-not-allowed">Next →</span>
        )}
      </div>
    </div>
  );
}
