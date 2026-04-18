// Server Component — pure display, no interactivity.
import type { FindingSeverity } from '@/types/scan';

const STYLES: Record<FindingSeverity, string> = {
  LOW:      'bg-sky-950     text-sky-400     ring-sky-800',
  MEDIUM:   'bg-yellow-950  text-yellow-400  ring-yellow-800',
  HIGH:     'bg-orange-950  text-orange-400  ring-orange-800',
  CRITICAL: 'bg-red-950     text-red-400     ring-red-800',
};

export default function SeverityBadge({ severity }: { severity: FindingSeverity }) {
  return (
    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ring-1 ring-inset ${STYLES[severity]}`}>
      {severity}
    </span>
  );
}
