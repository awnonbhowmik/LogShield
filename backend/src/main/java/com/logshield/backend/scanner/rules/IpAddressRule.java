package com.logshield.backend.scanner.rules;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class IpAddressRule extends AbstractRegexRule {

    // Strict IPv4: each octet 0-255, word boundaries prevent matching decimals mid-number
    private static final Pattern PATTERN = Pattern.compile(
            "\\b(?:(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}" +
            "(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\b"
    );

    @Override protected Pattern pattern()      { return PATTERN; }
    @Override protected String placeholder()   { return "[REDACTED_IP]"; }
    @Override public FindingCategory category() { return FindingCategory.IP_ADDRESS; }
    @Override public FindingSeverity severity() { return FindingSeverity.LOW; }
}
