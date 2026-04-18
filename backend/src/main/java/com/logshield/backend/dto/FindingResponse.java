package com.logshield.backend.dto;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;

public record FindingResponse(
        Long id,
        FindingCategory category,
        String matchedValue,
        String redactedValue,
        Integer lineNumber,
        FindingSeverity severity
) {}
