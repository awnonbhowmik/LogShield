import type { FindingSeverity } from '@/types/scan';

export function scoreToLevel(score: number | null): FindingSeverity | null {
  if (score === null || score === 0) return null;
  if (score <= 15) return 'LOW';
  if (score <= 40) return 'MEDIUM';
  if (score <= 75) return 'HIGH';
  return 'CRITICAL';
}
