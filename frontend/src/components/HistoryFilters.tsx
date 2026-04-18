'use client';

import { useRouter, usePathname } from 'next/navigation';
import { useCallback, useTransition } from 'react';

interface Props {
  initialSearch: string;
  currentPage: number;
  pageSize: number;
}

export default function HistoryFilters({ initialSearch, currentPage, pageSize }: Props) {
  const router = useRouter();
  const pathname = usePathname();
  const [isPending, startTransition] = useTransition();

  const push = useCallback((search: string, page = 0) => {
    const qs = new URLSearchParams();
    if (search) qs.set('search', search);
    if (page > 0) qs.set('page', String(page));
    if (pageSize !== 20) qs.set('size', String(pageSize));
    const query = qs.toString() ? `?${qs}` : '';
    startTransition(() => router.push(`${pathname}${query}`));
  }, [router, pathname, pageSize]);

  const hasFilters = initialSearch !== '';

  return (
    <div className="space-y-3">
      <div className="flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <svg className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-500 pointer-events-none"
            fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input
            type="text"
            placeholder="Search by filename…"
            defaultValue={initialSearch}
            onChange={(e) => push(e.target.value)}
            className={[
              'w-full rounded-xl border border-gray-700 bg-gray-900 pl-9 pr-4 py-2 text-sm text-gray-200',
              'placeholder-gray-500 focus:outline-none focus:border-blue-600 transition-colors',
              isPending ? 'opacity-60' : '',
            ].join(' ')}
          />
        </div>

        {hasFilters && (
          <button
            onClick={() => push('')}
            className="rounded-xl border border-gray-700 px-4 py-2 text-sm text-gray-400 hover:text-white hover:border-gray-500 transition-colors whitespace-nowrap"
          >
            Clear
          </button>
        )}
      </div>

      {isPending && (
        <p className="text-xs text-gray-500 animate-pulse">Filtering…</p>
      )}
    </div>
  );
}
