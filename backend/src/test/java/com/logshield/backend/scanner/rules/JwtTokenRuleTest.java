package com.logshield.backend.scanner.rules;

import com.logshield.backend.entity.ScanFinding.FindingSeverity;
import com.logshield.backend.scanner.RawMatch;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenRuleTest {

    private final JwtTokenRule rule = new JwtTokenRule();

    private static final String SAMPLE_JWT =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIn0" +
            ".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    @Test
    void detectsRealStructuredJwt() {
        List<RawMatch> matches = rule.detect("Authorization: Bearer " + SAMPLE_JWT);
        assertThat(matches).hasSize(1);
        assertThat(matches.get(0).matchedValue()).isEqualTo(SAMPLE_JWT);
        assertThat(matches.get(0).redactedValue()).isEqualTo("[REDACTED_JWT]");
        assertThat(matches.get(0).severity()).isEqualTo(FindingSeverity.HIGH);
    }

    @Test
    void doesNotMatchIncompleteToken() {
        // Only two parts — not a valid JWT structure
        assertThat(rule.detect("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0In0")).isEmpty();
    }

    @Test
    void doesNotMatchRandomBase64() {
        assertThat(rule.detect("aGVsbG8gd29ybGQ=")).isEmpty();
    }

    @Test
    void detectsJwtEmbeddedInLogLine() {
        String log = "2024-01-01 INFO  [auth] token=" + SAMPLE_JWT + " validated";
        List<RawMatch> matches = rule.detect(log);
        assertThat(matches).hasSize(1);
        assertThat(matches.get(0).lineNumber()).isEqualTo(1);
    }
}
