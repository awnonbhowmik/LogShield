package com.logshield.backend.scanner;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.scanner.rules.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DetectionEngineTest {

    private DetectionEngine engine;

    @BeforeEach
    void setUp() {
        List<DetectionRule> rules = List.of(
                new EmailRule(),
                new IpAddressRule(),
                new JwtTokenRule(),
                new ApiKeyRule(),
                new CreditCardRule()
        );
        engine = new DetectionEngine(rules, new RedactionService(), new DefaultSeverityScorer());
    }

    @Test
    void returnsEmptyResultForCleanText() {
        DetectionResult result = engine.scan("Nothing sensitive here.");
        assertThat(result.matches()).isEmpty();
        assertThat(result.totalFindings()).isEqualTo(0);
        assertThat(result.severityScore()).isEqualTo(0);
        assertThat(result.redactedContent()).isEqualTo("Nothing sensitive here.");
    }

    @Test
    void detectsAllCategoriesInMixedLogLine() {
        String log = """
                user=admin@corp.com ip=10.0.0.1 \
                card=4111111111111111 \
                key=sk-abcdefghijklmnopqrstuvwxyz123456
                """;

        DetectionResult result = engine.scan(log);

        assertThat(result.matches()).extracting(RawMatch::category)
                .contains(
                        FindingCategory.EMAIL,
                        FindingCategory.IP_ADDRESS,
                        FindingCategory.CREDIT_CARD,
                        FindingCategory.API_KEY
                );
        assertThat(result.totalFindings()).isGreaterThanOrEqualTo(4);
    }

    @Test
    void redactedContentContainsNoOriginalSensitiveValues() {
        String text = "email: test@example.com server: 192.168.0.1";
        DetectionResult result = engine.scan(text);

        assertThat(result.redactedContent()).doesNotContain("test@example.com");
        assertThat(result.redactedContent()).doesNotContain("192.168.0.1");
        assertThat(result.redactedContent()).contains("[REDACTED_EMAIL]");
        assertThat(result.redactedContent()).contains("[REDACTED_IP]");
    }

    @Test
    void deOverlapPreventsDoubleRedaction() {
        // A JWT also starts with characters that might match API key hex patterns;
        // de-overlapping should keep only one match per span.
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                     ".eyJzdWIiOiIxMjM0NTY3ODkwIn0" +
                     ".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        DetectionResult result = engine.scan("token: " + jwt);

        // Every character in the JWT belongs to exactly one match
        long jwtMatches = result.matches().stream()
                .filter(m -> m.matchedValue().equals(jwt))
                .count();
        assertThat(jwtMatches).isEqualTo(1);
        assertThat(result.redactedContent()).containsOnlyOnce("[REDACTED_JWT]");
    }

    @Test
    void computesSeverityScoreFromFindings() {
        String text = "api: sk-abcdef1234567890abcdef1234567890 card: 4111111111111111";
        DetectionResult result = engine.scan(text);
        // API_KEY (25) + CREDIT_CARD (25) = 50 → HIGH band
        assertThat(result.severityScore()).isGreaterThanOrEqualTo(50);
    }

    @Test
    void handlesEmptyInput() {
        DetectionResult result = engine.scan("");
        assertThat(result.matches()).isEmpty();
        assertThat(result.redactedContent()).isEmpty();
    }
}
