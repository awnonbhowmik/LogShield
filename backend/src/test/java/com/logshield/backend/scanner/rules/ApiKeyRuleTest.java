package com.logshield.backend.scanner.rules;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;
import com.logshield.backend.scanner.RawMatch;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiKeyRuleTest {

    private final ApiKeyRule rule = new ApiKeyRule();

    @Test
    void detectsPrefixedApiKey() {
        List<RawMatch> matches = rule.detect("config: api_key=sk-abcdefghijklmnopqrstuvwxyz123456");
        assertThat(matches).hasSize(1);
        assertThat(matches.get(0).category()).isEqualTo(FindingCategory.API_KEY);
        assertThat(matches.get(0).severity()).isEqualTo(FindingSeverity.CRITICAL);
        assertThat(matches.get(0).redactedValue()).isEqualTo("[REDACTED_API_KEY]");
    }

    @Test
    void detectsAwsAccessKey() {
        List<RawMatch> matches = rule.detect("AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE");
        assertThat(matches).hasSize(1);
        assertThat(matches.get(0).matchedValue()).isEqualTo("AKIAIOSFODNN7EXAMPLE");
    }

    @Test
    void detectsHexSecret() {
        // 32-char hex string commonly used as a secret/token
        List<RawMatch> matches = rule.detect("secret=d41d8cd98f00b204e9800998ecf8427e");
        assertThat(matches).hasSize(1);
    }

    @Test
    void ignoresShortHexString() {
        // Below 32 chars — not long enough to be a secret
        assertThat(rule.detect("id=abc123def456")).isEmpty();
    }

    @Test
    void detectsTokenPrefixedKey() {
        assertThat(rule.detect("token-abcdefghij1234567890ABCDEFGHIJ")).hasSize(1);
    }
}
