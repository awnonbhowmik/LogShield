// Server Component — static links, no browser APIs or state needed.
import Link from 'next/link';

export default function Navbar() {
  return (
    <nav className="sticky top-0 z-40 border-b border-gray-800 bg-gray-950/90 backdrop-blur-sm">
      <div className="mx-auto max-w-7xl flex items-center h-14 px-4 gap-6">
        {/* Logo */}
        <Link href="/" className="flex items-center gap-2 font-semibold text-white select-none">
          <svg className="h-5 w-5 text-blue-400" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round"
              d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
          </svg>
          LogShield
        </Link>

        {/* Nav links */}
        <div className="flex items-center gap-1">
          <NavLink href="/upload">Upload</NavLink>
          <NavLink href="/history">History</NavLink>
        </div>
      </div>
    </nav>
  );
}

function NavLink({ href, children }: { href: string; children: React.ReactNode }) {
  return (
    <Link
      href={href}
      className="px-3 py-1.5 text-sm text-gray-400 hover:text-white rounded-md hover:bg-gray-800 transition-colors"
    >
      {children}
    </Link>
  );
}
