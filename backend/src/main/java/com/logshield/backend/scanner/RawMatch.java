package com.logshield.backend.scanner;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;

/**
 * Immutable result of a single regex match.
 * start/end are character offsets used internally for de-overlapping before redaction.
 */
public record RawMatch(
        FindingCategory category,
        FindingSeverity severity,
        String matchedValue,
        String redactedValue,
        int lineNumber,
        int start,
        int end
) {}
