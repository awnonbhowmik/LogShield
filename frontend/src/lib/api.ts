import type {
  ScanDetailResponse,
  ScanSummaryResponse,
  ScanUploadResponse,
} from '@/types/scan';

export class ApiError extends Error {
  constructor(public readonly status: number, message: string) {
    super(message);
    this.name = 'ApiError';
  }
}

/**
 * Server components get an absolute URL so Node fetch can resolve it.
 * Client components use a relative path — Next.js rewrites proxy it to the backend.
 */
function url(path: string): string {
  if (typeof window === 'undefined') {
    const base = process.env.INTERNAL_API_URL ?? 'http://localhost:8080';
    return `${base}${path}`;
  }
  return path;
}

async function unwrap<T>(res: Response): Promise<T> {
  if (res.ok) return res.json() as Promise<T>;
  let message = `Request failed (${res.status})`;
  try {
    const body = await res.json();
    if (body?.message) message = body.message;
  } catch { /* ignore parse errors */ }
  throw new ApiError(res.status, message);
}

export async function uploadScan(file: File): Promise<ScanUploadResponse> {
  const form = new FormData();
  form.append('file', file);
  const res = await fetch(url('/api/scans'), { method: 'POST', body: form });
  return unwrap<ScanUploadResponse>(res);
}

export async function getScans(): Promise<ScanSummaryResponse[]> {
  const res = await fetch(url('/api/scans'), { cache: 'no-store' });
  return unwrap<ScanSummaryResponse[]>(res);
}

export async function getScan(id: number): Promise<ScanDetailResponse> {
  const res = await fetch(url(`/api/scans/${id}`), { cache: 'no-store' });
  return unwrap<ScanDetailResponse>(res);
}

export function downloadUrl(id: number): string {
  return `/api/scans/${id}/download`;
}
