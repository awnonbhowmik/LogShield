'use client';

import Link from 'next/link';

export default function ScanDetailError({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  return (
    <div className="mx-auto max-w-5xl px-4 py-24 text-center">
      <p className="text-4xl mb-4">⚠️</p>
      <h2 className="text-xl font-semibold text-white mb-2">Failed to load scan</h2>
      <p className="text-sm text-gray-400 mb-6">{error.message}</p>
      <div className="flex justify-center gap-3">
        <button
          onClick={reset}
          className="rounded-xl bg-blue-600 px-5 py-2 text-sm font-semibold text-white hover:bg-blue-500 transition-colors"
        >
          Try again
        </button>
        <Link href="/history" className="rounded-xl border border-gray-700 px-5 py-2 text-sm font-medium text-gray-300 hover:text-white transition-colors">
          Back to history
        </Link>
      </div>
    </div>
  );
}
