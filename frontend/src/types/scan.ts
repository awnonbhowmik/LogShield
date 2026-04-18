export type ScanStatus = 'PENDING' | 'COMPLETED' | 'FAILED';
export type FindingCategory = 'EMAIL' | 'IP_ADDRESS' | 'API_KEY' | 'JWT_TOKEN' | 'CREDIT_CARD';
export type FindingSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface FindingResponse {
  id: number;
  category: FindingCategory;
  matchedValue: string;
  redactedValue: string;
  lineNumber: number;
  severity: FindingSeverity;
}

export interface ScanSummaryResponse {
  id: number;
  filename: string;
  uploadedAt: string;
  status: ScanStatus;
  severityScore: number | null;
  findingCount: number;
}

export interface ScanDetailResponse {
  id: number;
  filename: string;
  uploadedAt: string;
  status: ScanStatus;
  severityScore: number | null;
  originalSize: number;
  redactedContent: string | null;
  findings: FindingResponse[];
}

export interface ScanUploadResponse {
  id: number;
  filename: string;
  uploadedAt: string;
  status: ScanStatus;
  severityScore: number | null;
  totalFindings: number;
  findingsByType: Partial<Record<FindingCategory, FindingResponse[]>>;
  redactedPreview: string | null;
}
