package com.logshield.backend.dto;

import com.logshield.backend.entity.ScanJob.ScanStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ScanDetailResponse(
        Long id,
        String filename,
        LocalDateTime uploadedAt,
        ScanStatus status,
        Integer severityScore,
        Long originalSize,
        String redactedContent,
        List<FindingResponse> findings
) {}
