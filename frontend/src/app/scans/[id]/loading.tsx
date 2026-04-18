// Shown by Next.js while scans/[id]/page.tsx is streaming.
export default function ScanDetailLoading() {
  return (
    <div className="mx-auto max-w-5xl px-4 py-12 space-y-8">
      {/* back link */}
      <div className="h-4 w-24 rounded bg-gray-800 animate-pulse" />

      {/* header */}
      <div className="space-y-2">
        <div className="h-7 w-72 rounded-lg bg-gray-800 animate-pulse" />
        <div className="h-4 w-40 rounded bg-gray-800 animate-pulse" />
      </div>

      {/* stat strip */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        {[...Array(4)].map((_, i) => (
          <div key={i} className="rounded-xl border border-gray-800 bg-gray-900 p-4 space-y-2">
            <div className="h-3 w-16 rounded bg-gray-800 animate-pulse" />
            <div className="h-6 w-12 rounded bg-gray-800 animate-pulse" />
          </div>
        ))}
      </div>

      {/* findings table skeleton */}
      <div className="rounded-xl border border-gray-800 bg-gray-900 overflow-hidden">
        {[...Array(4)].map((_, i) => (
          <div key={i} className="flex gap-4 px-4 py-3 border-b border-gray-800 last:border-0">
            <div className="h-4 w-24 rounded bg-gray-800 animate-pulse" />
            <div className="h-4 w-8  rounded bg-gray-800 animate-pulse" />
            <div className="h-4 w-40 rounded bg-gray-800 animate-pulse" />
            <div className="h-4 w-32 rounded bg-gray-800 animate-pulse" />
          </div>
        ))}
      </div>

      {/* preview skeleton */}
      <div className="rounded-xl border border-gray-800 bg-gray-900 p-4 space-y-2">
        {[...Array(6)].map((_, i) => (
          <div key={i} className="h-3 rounded bg-gray-800 animate-pulse" style={{ width: `${60 + Math.random() * 35}%` }} />
        ))}
      </div>
    </div>
  );
}
