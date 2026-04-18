package com.logshield.backend.scanner;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Category-weighted scoring: each finding type contributes a fixed number of points.
 * High-risk credentials (API keys, cards) contribute far more than low-risk identifiers.
 *
 * Score bands:
 *   0        → no findings
 *   1–15     → LOW
 *   16–40    → MEDIUM
 *   41–75    → HIGH
 *   76–100   → CRITICAL
 */
@Component
public class DefaultSeverityScorer implements SeverityScorer {

    private static final Map<FindingCategory, Integer> WEIGHTS = Map.of(
            FindingCategory.EMAIL,                5,
            FindingCategory.IP_ADDRESS,           5,
            FindingCategory.JWT_TOKEN,           20,
            FindingCategory.API_KEY,             25,
            FindingCategory.CREDIT_CARD,         25,
            FindingCategory.DB_CONNECTION_STRING, 30
    );

    @Override
    public int score(List<RawMatch> matches) {
        int total = matches.stream()
                .mapToInt(m -> WEIGHTS.getOrDefault(m.category(), 5))
                .sum();
        return Math.min(100, total);
    }
}
