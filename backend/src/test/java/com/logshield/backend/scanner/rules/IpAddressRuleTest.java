package com.logshield.backend.scanner.rules;

import com.logshield.backend.scanner.RawMatch;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IpAddressRuleTest {

    private final IpAddressRule rule = new IpAddressRule();

    @Test
    void detectsTypicalPrivateIp() {
        List<RawMatch> matches = rule.detect("server at 192.168.1.1 responded");
        assertThat(matches).hasSize(1);
        assertThat(matches.get(0).matchedValue()).isEqualTo("192.168.1.1");
        assertThat(matches.get(0).redactedValue()).isEqualTo("[REDACTED_IP]");
    }

    @Test
    void detectsPublicIp() {
        List<RawMatch> matches = rule.detect("origin: 8.8.8.8");
        assertThat(matches).hasSize(1);
        assertThat(matches.get(0).matchedValue()).isEqualTo("8.8.8.8");
    }

    @Test
    void detectsBoundaryOctetValues() {
        List<RawMatch> matches = rule.detect("0.0.0.0 and 255.255.255.255");
        assertThat(matches).hasSize(2);
    }

    @Test
    void rejectsInvalidOctet() {
        assertThat(rule.detect("999.999.999.999")).isEmpty();
        assertThat(rule.detect("256.1.1.1")).isEmpty();
    }

    @Test
    void doesNotMatchVersionNumber() {
        // "1.0.0.0" is a valid IP, but "1.0.0" with only 3 octets should not match
        assertThat(rule.detect("version 3.0.1 released")).isEmpty();
    }
}
