package com.logshield.backend.scanner;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RedactionServiceTest {

    private final RedactionService service = new RedactionService();

    @Test
    void redactsSingleMatch() {
        String text = "Email: user@example.com here";
        RawMatch match = new RawMatch(
                FindingCategory.EMAIL, FindingSeverity.LOW,
                "user@example.com", "[REDACTED_EMAIL]",
                1, 7, 23
        );

        String result = service.redact(text, List.of(match));
        assertThat(result).isEqualTo("Email: [REDACTED_EMAIL] here");
    }

    @Test
    void redactsMultipleMatchesCorrectly() {
        String text = "ip=1.2.3.4 email=a@b.com";
        // pre-computed offsets
        RawMatch ipMatch = new RawMatch(
                FindingCategory.IP_ADDRESS, FindingSeverity.LOW,
                "1.2.3.4", "[REDACTED_IP]",
                1, 3, 10
        );
        RawMatch emailMatch = new RawMatch(
                FindingCategory.EMAIL, FindingSeverity.LOW,
                "a@b.com", "[REDACTED_EMAIL]",
                1, 17, 24
        );

        String result = service.redact(text, List.of(ipMatch, emailMatch));
        assertThat(result).isEqualTo("ip=[REDACTED_IP] email=[REDACTED_EMAIL]");
    }

    @Test
    void returnsOriginalTextWhenNoMatches() {
        String text = "nothing sensitive here";
        assertThat(service.redact(text, List.of())).isEqualTo(text);
    }

    @Test
    void handlesAdjacentMatches() {
        // Two matches side by side with no gap
        String text = "AKIAIOSFODNN7EXAMPLE1.2.3.4";
        RawMatch key = new RawMatch(FindingCategory.API_KEY, FindingSeverity.CRITICAL,
                "AKIAIOSFODNN7EXAMPLE", "[REDACTED_API_KEY]", 1, 0, 20);
        RawMatch ip = new RawMatch(FindingCategory.IP_ADDRESS, FindingSeverity.LOW,
                "1.2.3.4", "[REDACTED_IP]", 1, 20, 27);

        String result = service.redact(text, List.of(key, ip));
        assertThat(result).isEqualTo("[REDACTED_API_KEY][REDACTED_IP]");
    }
}
