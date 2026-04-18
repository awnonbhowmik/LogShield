package com.logshield.backend.dto;

import java.util.List;

public record PagedScanResponse(
        List<ScanSummaryResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
