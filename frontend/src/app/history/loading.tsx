// Shown by Next.js while history/page.tsx is streaming.
export default function HistoryLoading() {
  return (
    <div className="mx-auto max-w-5xl px-4 py-12">
      <div className="mb-8">
        <div className="h-7 w-40 rounded-lg bg-gray-800 animate-pulse mb-2" />
        <div className="h-4 w-24 rounded bg-gray-800 animate-pulse" />
      </div>
      <div className="rounded-xl border border-gray-800 bg-gray-900 overflow-hidden">
        {[...Array(5)].map((_, i) => (
          <div key={i} className="flex gap-4 px-4 py-3 border-b border-gray-800 last:border-0">
            <div className="h-4 w-48 rounded bg-gray-800 animate-pulse" />
            <div className="h-4 w-32 rounded bg-gray-800 animate-pulse" />
            <div className="h-4 w-20 rounded bg-gray-800 animate-pulse" />
            <div className="h-4 w-16 rounded bg-gray-800 animate-pulse ml-auto" />
          </div>
        ))}
      </div>
    </div>
  );
}
