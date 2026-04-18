'use client';
// Client Component — needs clipboard API and useState for copy feedback.

import { useState } from 'react';

export default function RedactedPreview({ content }: { content: string | null }) {
  const [copied, setCopied] = useState(false);

  if (!content) {
    return (
      <div className="rounded-xl border border-gray-800 bg-gray-900 px-6 py-10 text-center text-gray-500 text-sm">
        No redacted content available.
      </div>
    );
  }

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(content);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch {
      // clipboard not available
    }
  };

  return (
    <div className="rounded-xl border border-gray-800 bg-gray-900 overflow-hidden">
      {/* Header */}
      <div className="flex items-center justify-between px-4 py-3 border-b border-gray-800">
        <span className="text-xs font-medium uppercase tracking-wide text-gray-500">Redacted Output</span>
        <button
          onClick={handleCopy}
          className="flex items-center gap-1.5 rounded-md px-2.5 py-1 text-xs font-medium text-gray-400 hover:text-white hover:bg-gray-700 transition-colors"
        >
          {copied ? (
            <>
              <svg className="h-3.5 w-3.5 text-emerald-400" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
              </svg>
              <span className="text-emerald-400">Copied</span>
            </>
          ) : (
            <>
              <svg className="h-3.5 w-3.5" viewBox="0 0 20 20" fill="currentColor">
                <path d="M8 3a1 1 0 011-1h2a1 1 0 110 2H9a1 1 0 01-1-1z" />
                <path d="M6 3a2 2 0 00-2 2v11a2 2 0 002 2h8a2 2 0 002-2V5a2 2 0 00-2-2 3 3 0 01-3 3H9a3 3 0 01-3-3z" />
              </svg>
              Copy
            </>
          )}
        </button>
      </div>

      {/* Content */}
      <pre className="overflow-x-auto p-4 text-xs leading-relaxed text-gray-300 whitespace-pre-wrap max-h-96">
        {content}
      </pre>
    </div>
  );
}
