import type { ScanDetailResponse, ScanSummaryResponse, ScanUploadResponse } from '@/types/scan';

export const MOCK_SCANS: ScanSummaryResponse[] = [
  {
    id: 1,
    filename: 'server-2024-01-15.log',
    uploadedAt: '2024-01-15T10:30:00',
    status: 'COMPLETED',
    severityScore: 75,
    findingCount: 8,
  },
  {
    id: 2,
    filename: 'auth-debug.log',
    uploadedAt: '2024-01-14T14:22:00',
    status: 'COMPLETED',
    severityScore: 25,
    findingCount: 3,
  },
  {
    id: 3,
    filename: 'app-errors.txt',
    uploadedAt: '2024-01-13T09:15:00',
    status: 'COMPLETED',
    severityScore: 50,
    findingCount: 5,
  },
  {
    id: 4,
    filename: 'access.log',
    uploadedAt: '2024-01-12T16:45:00',
    status: 'FAILED',
    severityScore: null,
    findingCount: 0,
  },
];

export const MOCK_DETAIL: ScanDetailResponse = {
  id: 1,
  filename: 'server-2024-01-15.log',
  uploadedAt: '2024-01-15T10:30:00',
  status: 'COMPLETED',
  severityScore: 75,
  originalSize: 4096,
  redactedContent: `2024-01-15 10:00:01 INFO  User login: user=[REDACTED_EMAIL] ip=[REDACTED_IP]
2024-01-15 10:00:05 DEBUG Request headers: Authorization=Bearer [REDACTED_JWT]
2024-01-15 10:01:12 INFO  Payment processed for card [REDACTED_CARD]
2024-01-15 10:02:30 ERROR Failed auth attempt from [REDACTED_IP]
2024-01-15 10:03:00 DEBUG Config loaded: api_key=[REDACTED_API_KEY]
2024-01-15 10:04:45 INFO  New user registered: [REDACTED_EMAIL]
2024-01-15 10:05:10 WARN  Rate limit hit from [REDACTED_IP]
2024-01-15 10:06:00 INFO  Refund issued for [REDACTED_CARD]`,
  findings: [
    { id: 1, category: 'EMAIL', matchedValue: 'john.doe@acme.com', redactedValue: '[REDACTED_EMAIL]', lineNumber: 1, severity: 'LOW' },
    { id: 2, category: 'IP_ADDRESS', matchedValue: '192.168.1.101', redactedValue: '[REDACTED_IP]', lineNumber: 1, severity: 'LOW' },
    { id: 3, category: 'JWT_TOKEN', matchedValue: 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSJ9.abc123', redactedValue: '[REDACTED_JWT]', lineNumber: 2, severity: 'HIGH' },
    { id: 4, category: 'CREDIT_CARD', matchedValue: '4111111111111111', redactedValue: '[REDACTED_CARD]', lineNumber: 3, severity: 'CRITICAL' },
    { id: 5, category: 'IP_ADDRESS', matchedValue: '10.0.0.55', redactedValue: '[REDACTED_IP]', lineNumber: 4, severity: 'LOW' },
    { id: 6, category: 'API_KEY', matchedValue: 'sk-abcdef1234567890abcdef12345678', redactedValue: '[REDACTED_API_KEY]', lineNumber: 5, severity: 'CRITICAL' },
    { id: 7, category: 'EMAIL', matchedValue: 'jane.smith@acme.com', redactedValue: '[REDACTED_EMAIL]', lineNumber: 6, severity: 'LOW' },
    { id: 8, category: 'CREDIT_CARD', matchedValue: '5500005555555559', redactedValue: '[REDACTED_CARD]', lineNumber: 8, severity: 'CRITICAL' },
  ],
};

export const MOCK_UPLOAD_RESPONSE: ScanUploadResponse = {
  id: 1,
  filename: 'server-2024-01-15.log',
  uploadedAt: '2024-01-15T10:30:00',
  status: 'COMPLETED',
  severityScore: 75,
  totalFindings: 8,
  findingsByType: {
    EMAIL:       MOCK_DETAIL.findings.filter(f => f.category === 'EMAIL'),
    IP_ADDRESS:  MOCK_DETAIL.findings.filter(f => f.category === 'IP_ADDRESS'),
    JWT_TOKEN:   MOCK_DETAIL.findings.filter(f => f.category === 'JWT_TOKEN'),
    API_KEY:     MOCK_DETAIL.findings.filter(f => f.category === 'API_KEY'),
    CREDIT_CARD: MOCK_DETAIL.findings.filter(f => f.category === 'CREDIT_CARD'),
  },
  redactedPreview: MOCK_DETAIL.redactedContent,
};

export function getMockScan(id: number): ScanDetailResponse | null {
  if (id === 1) return MOCK_DETAIL;
  const summary = MOCK_SCANS.find(s => s.id === id);
  if (!summary) return null;
  return {
    id: summary.id,
    filename: summary.filename,
    uploadedAt: summary.uploadedAt,
    status: summary.status,
    severityScore: summary.severityScore,
    originalSize: 1024,
    redactedContent: summary.status === 'COMPLETED' ? 'Redacted content for scan #' + id : null,
    findings: [],
  };
}
