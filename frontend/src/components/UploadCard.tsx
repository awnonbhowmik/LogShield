'use client';
// Client Component — file input, drag/drop, API call, navigation.

import { useState, useRef, useCallback } from 'react';
import { useRouter } from 'next/navigation';
import { uploadScan, ApiError } from '@/lib/api';

type UploadState = 'idle' | 'selected' | 'scanning' | 'error';

export default function UploadCard() {
  const router = useRouter();
  const [state, setState] = useState<UploadState>('idle');
  const [dragging, setDragging] = useState(false);
  const [file, setFile] = useState<File | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const selectFile = (f: File) => {
    const ext = f.name.split('.').pop()?.toLowerCase();
    if (ext !== 'txt' && ext !== 'log') {
      setErrorMsg('Only .txt and .log files are accepted.');
      setState('error');
      return;
    }
    setFile(f);
    setErrorMsg(null);
    setState('selected');
  };

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault();
    setDragging(false);
    const f = e.dataTransfer.files[0];
    if (f) selectFile(f);
  }, []);

  const handleFileInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    const f = e.target.files?.[0];
    if (f) selectFile(f);
  };

  const handleScan = async () => {
    if (!file) return;
    setState('scanning');
    setErrorMsg(null);
    try {
      const result = await uploadScan(file);
      router.push(`/scans/${result.id}`);
    } catch (err) {
      const msg =
        err instanceof ApiError
          ? err.message
          : 'Something went wrong. Please try again.';
      setErrorMsg(msg);
      setState('error');
    }
  };

  const handleReset = () => {
    setState('idle');
    setFile(null);
    setErrorMsg(null);
    if (inputRef.current) inputRef.current.value = '';
  };

  const clickable = state === 'idle' || state === 'error' || state === 'selected';

  return (
    <div className="space-y-4">
      {/* Drop zone */}
      <div
        onDragOver={(e) => { e.preventDefault(); setDragging(true); }}
        onDragLeave={() => setDragging(false)}
        onDrop={handleDrop}
        onClick={() => clickable && inputRef.current?.click()}
        className={[
          'relative rounded-2xl border-2 border-dashed p-12 text-center transition-all',
          clickable ? 'cursor-pointer' : 'cursor-default',
          dragging
            ? 'border-blue-500 bg-blue-950/20'
            : 'border-gray-700 hover:border-gray-600 bg-gray-900/50',
        ].join(' ')}
      >
        <input
          ref={inputRef}
          type="file"
          accept=".txt,.log"
          className="hidden"
          onChange={handleFileInput}
        />

        {state === 'scanning' ? (
          <ScanningSpinner />
        ) : (
          <IdleState filename={file?.name ?? null} error={errorMsg} />
        )}
      </div>

      {/* Actions */}
      {state === 'selected' && (
        <button
          onClick={handleScan}
          className="w-full rounded-xl bg-blue-600 py-3 text-sm font-semibold text-white hover:bg-blue-500 active:bg-blue-700 transition-colors"
        >
          Scan File
        </button>
      )}

      {(state === 'error') && (
        <button
          onClick={handleReset}
          className="w-full rounded-xl border border-gray-700 py-3 text-sm font-medium text-gray-400 hover:text-white hover:border-gray-500 transition-colors"
        >
          Try again
        </button>
      )}
    </div>
  );
}

function IdleState({ filename, error }: { filename: string | null; error: string | null }) {
  return (
    <>
      <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-gray-800">
        <svg className="h-6 w-6 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
          <path strokeLinecap="round" strokeLinejoin="round"
            d="M3 16.5v2.25A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75V16.5m-13.5-9L12 3m0 0l4.5 4.5M12 3v13.5" />
        </svg>
      </div>
      {filename ? (
        <p className="text-sm font-medium text-white">{filename}</p>
      ) : (
        <>
          <p className="text-sm font-medium text-gray-300">Drop a file here, or click to browse</p>
          <p className="mt-1 text-xs text-gray-500">Accepts .txt and .log — up to 10 MB</p>
        </>
      )}
      {error && (
        <p className="mt-3 text-xs text-red-400">{error}</p>
      )}
    </>
  );
}

function ScanningSpinner() {
  return (
    <div className="flex flex-col items-center gap-3">
      <div className="h-8 w-8 animate-spin rounded-full border-2 border-blue-600 border-t-transparent" />
      <p className="text-sm text-gray-400">Scanning for sensitive data…</p>
    </div>
  );
}
