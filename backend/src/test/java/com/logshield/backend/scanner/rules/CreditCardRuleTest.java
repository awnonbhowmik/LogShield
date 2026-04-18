package com.logshield.backend.scanner.rules;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;
import com.logshield.backend.scanner.RawMatch;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CreditCardRuleTest {

    private final CreditCardRule rule = new CreditCardRule();

    @Test
    void detectsVisa16DigitNoSeparators() {
        List<RawMatch> matches = rule.detect("card: 4111111111111111 charged");
        assertThat(matches).hasSize(1);
        assertThat(matches.get(0).category()).isEqualTo(FindingCategory.CREDIT_CARD);
        assertThat(matches.get(0).severity()).isEqualTo(FindingSeverity.CRITICAL);
        assertThat(matches.get(0).redactedValue()).isEqualTo("[REDACTED_CARD]");
    }

    @Test
    void detectsVisaWithDashes() {
        List<RawMatch> matches = rule.detect("4111-1111-1111-1111");
        assertThat(matches).hasSize(1);
    }

    @Test
    void detectsVisaWithSpaces() {
        List<RawMatch> matches = rule.detect("4111 1111 1111 1111");
        assertThat(matches).hasSize(1);
    }

    @Test
    void detectsMastercard() {
        List<RawMatch> matches = rule.detect("mc: 5500005555555559");
        assertThat(matches).hasSize(1);
    }

    @Test
    void detectsAmex() {
        List<RawMatch> matches = rule.detect("amex: 371449635398431");
        assertThat(matches).hasSize(1);
    }

    @Test
    void ignoresRandomDigitSequence() {
        // 16 digits but not starting with a valid card prefix
        assertThat(rule.detect("ref: 9999999999999999")).isEmpty();
    }
}
