package com.logshield.backend.dto;

import com.logshield.backend.entity.ScanJob.ScanStatus;

import java.time.LocalDateTime;

public record ScanSummaryResponse(
        Long id,
        String filename,
        LocalDateTime uploadedAt,
        ScanStatus status,
        Integer severityScore,
        int findingCount
) {}
