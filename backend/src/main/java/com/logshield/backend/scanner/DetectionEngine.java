package com.logshield.backend.scanner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Orchestrates all registered DetectionRule beans.
 * Steps:
 *   1. Run every rule against the input text.
 *   2. Sort all matches by start offset and remove overlapping spans (first match wins).
 *   3. Redact the de-overlapped matches.
 *   4. Compute an aggregate severity score.
 */
@Component
@RequiredArgsConstructor
public class DetectionEngine {

    private final List<DetectionRule> rules;
    private final RedactionService redactionService;
    private final SeverityScorer severityScorer;

    public DetectionResult scan(String text) {
        List<RawMatch> allMatches = new ArrayList<>();
        for (DetectionRule rule : rules) {
            allMatches.addAll(rule.detect(text));
        }

        List<RawMatch> deduped = deOverlap(allMatches);
        String redacted = redactionService.redact(text, deduped);
        int score = severityScorer.score(deduped);

        return new DetectionResult(deduped, redacted, deduped.size(), score);
    }

    /** Keeps the first match when two spans overlap (sorted by start position). */
    private static List<RawMatch> deOverlap(List<RawMatch> matches) {
        List<RawMatch> result = new ArrayList<>();
        int lastEnd = -1;

        for (RawMatch m : matches.stream().sorted(Comparator.comparingInt(RawMatch::start)).toList()) {
            if (m.start() >= lastEnd) {
                result.add(m);
                lastEnd = m.end();
            }
        }
        return result;
    }
}
