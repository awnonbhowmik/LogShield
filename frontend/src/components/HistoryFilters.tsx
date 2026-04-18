'use client';
// Client Component — owns filter state; renders HistoryTable with the filtered subset.

import { useState, useMemo } from 'react';
import type { ScanSummaryResponse, FindingSeverity } from '@/types/scan';
import HistoryTable from './HistoryTable';

type SeverityFilter = FindingSeverity | 'ALL';

function scoreToLevel(score: number | null): FindingSeverity | null {
  if (!score) return null;
  if (score <= 15) return 'LOW';
  if (score <= 40) return 'MEDIUM';
  if (score <= 75) return 'HIGH';
  return 'CRITICAL';
}

export default function HistoryFilters({ scans }: { scans: ScanSummaryResponse[] }) {
  const [search, setSearch]     = useState('');
  const [severity, setSeverity] = useState<SeverityFilter>('ALL');
  const [fromDate, setFromDate] = useState('');

  const filtered = useMemo(() => {
    return scans.filter((s) => {
      if (search && !s.filename.toLowerCase().includes(search.toLowerCase())) return false;
      if (severity !== 'ALL' && scoreToLevel(s.severityScore) !== severity) return false;
      if (fromDate && new Date(s.uploadedAt) < new Date(fromDate)) return false;
      return true;
    });
  }, [scans, search, severity, fromDate]);

  const hasFilters = search !== '' || severity !== 'ALL' || fromDate !== '';

  return (
    <div className="space-y-4">
      {/* Filter bar */}
      <div className="flex flex-col sm:flex-row gap-3">
        {/* Filename search */}
        <div className="relative flex-1">
          <svg className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-500 pointer-events-none"
            fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input
            type="text"
            placeholder="Search by filename…"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full rounded-xl border border-gray-700 bg-gray-900 pl-9 pr-4 py-2 text-sm text-gray-200 placeholder-gray-500 focus:outline-none focus:border-blue-600 transition-colors"
          />
        </div>

        {/* Severity filter */}
        <select
          value={severity}
          onChange={(e) => setSeverity(e.target.value as SeverityFilter)}
          className="rounded-xl border border-gray-700 bg-gray-900 px-3 py-2 text-sm text-gray-300 focus:outline-none focus:border-blue-600 transition-colors"
        >
          <option value="ALL">All severities</option>
          <option value="CRITICAL">Critical</option>
          <option value="HIGH">High</option>
          <option value="MEDIUM">Medium</option>
          <option value="LOW">Low</option>
        </select>

        {/* From date */}
        <input
          type="date"
          value={fromDate}
          onChange={(e) => setFromDate(e.target.value)}
          className="rounded-xl border border-gray-700 bg-gray-900 px-3 py-2 text-sm text-gray-300 focus:outline-none focus:border-blue-600 transition-colors"
        />

        {/* Clear */}
        {hasFilters && (
          <button
            onClick={() => { setSearch(''); setSeverity('ALL'); setFromDate(''); }}
            className="rounded-xl border border-gray-700 px-4 py-2 text-sm text-gray-400 hover:text-white hover:border-gray-500 transition-colors whitespace-nowrap"
          >
            Clear
          </button>
        )}
      </div>

      {/* Result count */}
      {hasFilters && (
        <p className="text-xs text-gray-500">
          {filtered.length} of {scans.length} scans match
        </p>
      )}

      <HistoryTable scans={filtered} />
    </div>
  );
}
