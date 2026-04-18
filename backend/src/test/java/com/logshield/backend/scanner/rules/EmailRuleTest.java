package com.logshield.backend.scanner.rules;

import com.logshield.backend.scanner.RawMatch;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EmailRuleTest {

    private final EmailRule rule = new EmailRule();

    @Test
    void detectsSingleEmail() {
        List<RawMatch> matches = rule.detect("Contact us at admin@example.com for support.");
        assertThat(matches).hasSize(1);
        assertThat(matches.get(0).matchedValue()).isEqualTo("admin@example.com");
        assertThat(matches.get(0).redactedValue()).isEqualTo("[REDACTED_EMAIL]");
    }

    @Test
    void detectsMultipleEmailsOnDifferentLines() {
        String text = "From: alice@corp.io\nTo: bob@corp.io\nCC: carol@other.org";
        List<RawMatch> matches = rule.detect(text);
        assertThat(matches).hasSize(3);
        assertThat(matches.get(0).lineNumber()).isEqualTo(1);
        assertThat(matches.get(1).lineNumber()).isEqualTo(2);
        assertThat(matches.get(2).lineNumber()).isEqualTo(3);
    }

    @Test
    void ignoresPlainWords() {
        assertThat(rule.detect("no emails here")).isEmpty();
    }

    @Test
    void ignoresMissingDomain() {
        assertThat(rule.detect("user@")).isEmpty();
    }

    @Test
    void handlesSubdomainEmail() {
        List<RawMatch> matches = rule.detect("reach user@mail.internal.corp.com now");
        assertThat(matches).hasSize(1);
    }
}
