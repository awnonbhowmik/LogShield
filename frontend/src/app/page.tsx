// Server Component — static landing page, no data fetching or state.
import Link from 'next/link';

const FEATURES = [
  { icon: '📧', label: 'Email addresses' },
  { icon: '🌐', label: 'IPv4 addresses' },
  { icon: '🔑', label: 'API keys & secrets' },
  { icon: '🪙', label: 'JWT tokens' },
  { icon: '💳', label: 'Credit card numbers' },
];

export default function HomePage() {
  return (
    <div className="mx-auto max-w-4xl px-4 py-24 text-center">
      {/* Badge */}
      <div className="inline-flex items-center gap-2 rounded-full border border-blue-800 bg-blue-950/40 px-3 py-1 text-xs text-blue-400 mb-8">
        <span className="h-1.5 w-1.5 rounded-full bg-blue-400" />
        Open source · Free to use
      </div>

      {/* Headline */}
      <h1 className="text-4xl sm:text-5xl font-bold text-white leading-tight tracking-tight mb-4">
        Detect and redact{' '}
        <span className="text-blue-400">sensitive data</span>
        <br />in your log files
      </h1>
      <p className="text-lg text-gray-400 max-w-xl mx-auto mb-10">
        Upload a <code className="text-gray-300">.log</code> or{' '}
        <code className="text-gray-300">.txt</code> file. LogShield scans it for
        credentials, emails, and IP addresses, then returns a clean redacted copy.
      </p>

      {/* CTAs */}
      <div className="flex flex-wrap justify-center gap-3 mb-16">
        <Link
          href="/upload"
          className="rounded-xl bg-blue-600 px-6 py-3 text-sm font-semibold text-white hover:bg-blue-500 transition-colors"
        >
          Upload a file →
        </Link>
        <Link
          href="/history"
          className="rounded-xl border border-gray-700 px-6 py-3 text-sm font-semibold text-gray-300 hover:border-gray-600 hover:text-white transition-colors"
        >
          View history
        </Link>
      </div>

      {/* Detection list */}
      <div className="rounded-2xl border border-gray-800 bg-gray-900/50 p-8">
        <p className="text-xs font-medium uppercase tracking-wide text-gray-500 mb-5">Detects</p>
        <div className="flex flex-wrap justify-center gap-3">
          {FEATURES.map((f) => (
            <span
              key={f.label}
              className="flex items-center gap-2 rounded-full border border-gray-700 bg-gray-800 px-4 py-2 text-sm text-gray-300"
            >
              <span>{f.icon}</span>
              {f.label}
            </span>
          ))}
        </div>
      </div>
    </div>
  );
}
