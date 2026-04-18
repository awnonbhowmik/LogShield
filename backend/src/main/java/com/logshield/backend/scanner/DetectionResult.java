package com.logshield.backend.scanner;

import java.util.List;

/**
 * Aggregate output of one full scan pass through DetectionEngine.
 */
public record DetectionResult(
        List<RawMatch> matches,
        String redactedContent,
        int totalFindings,
        int severityScore
) {}
