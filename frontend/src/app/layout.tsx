// Server Component — root layout; no state or browser APIs needed.
import type { Metadata } from 'next';
import { Geist, Geist_Mono } from 'next/font/google';
import './globals.css';
import Navbar from '@/components/Navbar';

const geistSans = Geist({ variable: '--font-geist-sans', subsets: ['latin'] });
const geistMono = Geist_Mono({ variable: '--font-geist-mono', subsets: ['latin'] });

export const metadata: Metadata = {
  title: 'LogShield — Sensitive Data Redaction',
  description: 'Upload log files, detect and redact sensitive data, and review scan history.',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className={`${geistSans.variable} ${geistMono.variable} h-full`}>
      <body className="min-h-full flex flex-col bg-gray-950 text-gray-100 antialiased">
        <Navbar />
        <main className="flex-1">{children}</main>
      </body>
    </html>
  );
}
