// Server Component — rendered by Next.js when notFound() is called.
import Link from 'next/link';

export default function NotFound() {
  return (
    <div className="mx-auto max-w-xl px-4 py-32 text-center">
      <p className="text-6xl font-bold text-gray-800 mb-4">404</p>
      <h1 className="text-xl font-semibold text-white mb-2">Page not found</h1>
      <p className="text-sm text-gray-400 mb-8">The scan or page you&apos;re looking for doesn&apos;t exist.</p>
      <div className="flex justify-center gap-3">
        <Link href="/upload" className="rounded-xl bg-blue-600 px-5 py-2 text-sm font-semibold text-white hover:bg-blue-500 transition-colors">
          Upload a file
        </Link>
        <Link href="/history" className="rounded-xl border border-gray-700 px-5 py-2 text-sm font-medium text-gray-300 hover:text-white transition-colors">
          View history
        </Link>
      </div>
    </div>
  );
}
