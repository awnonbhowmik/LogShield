package com.logshield.backend.dto;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanJob.ScanStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Returned by POST /api/scans.
 * findingsByType groups all matches by category for the summary cards.
 * redactedPreview is capped at 1000 characters; the full file is available via GET /{id}/download.
 */
public record ScanUploadResponse(
        Long id,
        String filename,
        LocalDateTime uploadedAt,
        ScanStatus status,
        Integer severityScore,
        int totalFindings,
        Map<FindingCategory, List<FindingResponse>> findingsByType,
        String redactedPreview
) {}
