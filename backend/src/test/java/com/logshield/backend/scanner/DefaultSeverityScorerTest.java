package com.logshield.backend.scanner;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultSeverityScorerTest {

    private final DefaultSeverityScorer scorer = new DefaultSeverityScorer();

    private static RawMatch matchOf(FindingCategory category) {
        return new RawMatch(category, FindingSeverity.LOW, "x", "[X]", 1, 0, 1);
    }

    @Test
    void returnsZeroForEmptyList() {
        assertThat(scorer.score(List.of())).isEqualTo(0);
    }

    @Test
    void scoresSingleEmailAsLow() {
        int score = scorer.score(List.of(matchOf(FindingCategory.EMAIL)));
        assertThat(score).isEqualTo(5); // below LOW band ceiling of 15
    }

    @Test
    void scoresSingleJwtAsMedium() {
        int score = scorer.score(List.of(matchOf(FindingCategory.JWT_TOKEN)));
        assertThat(score).isEqualTo(20); // 16–40 → MEDIUM
    }

    @Test
    void scoresSingleApiKeyAsMedium() {
        int score = scorer.score(List.of(matchOf(FindingCategory.API_KEY)));
        assertThat(score).isEqualTo(25);
    }

    @Test
    void scoresMultipleHighRiskFindings() {
        List<RawMatch> matches = List.of(
                matchOf(FindingCategory.API_KEY),     // 25
                matchOf(FindingCategory.CREDIT_CARD), // 25
                matchOf(FindingCategory.JWT_TOKEN)    // 20
        );
        assertThat(scorer.score(matches)).isEqualTo(70); // HIGH band
    }

    @Test
    void capsScoreAt100() {
        List<RawMatch> matches = List.of(
                matchOf(FindingCategory.API_KEY),     // 25
                matchOf(FindingCategory.API_KEY),     // 25
                matchOf(FindingCategory.CREDIT_CARD), // 25
                matchOf(FindingCategory.CREDIT_CARD), // 25
                matchOf(FindingCategory.JWT_TOKEN)    // 20 → total 120 → capped 100
        );
        assertThat(scorer.score(matches)).isEqualTo(100);
    }
}
