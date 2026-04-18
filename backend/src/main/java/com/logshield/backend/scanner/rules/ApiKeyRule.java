package com.logshield.backend.scanner.rules;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ApiKeyRule extends AbstractRegexRule {

    // Matches:
    //   1. Common prefixed keys  e.g. sk-abc123..., api_key-XYZ...
    //   2. AWS IAM permanent access key IDs  e.g. AKIAIOSFODNN7EXAMPLE
    //   3. AWS STS temporary credentials    e.g. ASIAIOSFODNN7EXAMPLE
    // Note: bare hex strings removed — too broad (matches hashes) and a ReDoS risk.
    private static final Pattern PATTERN = Pattern.compile(
            "\\b(?:" +
            "(?:sk|pk|api|key|token|secret|access|bearer|auth)[_\\-=][A-Za-z0-9_\\-]{16,64}" +
            "|(?:AKIA|ASIA|AROA|AIDA|AIPA|ANPA|ANVA|APKA)[0-9A-Z]{16}" +
            ")\\b"
    );

    @Override protected Pattern pattern()      { return PATTERN; }
    @Override protected String placeholder()   { return "[REDACTED_API_KEY]"; }
    @Override public FindingCategory category() { return FindingCategory.API_KEY; }
    @Override public FindingSeverity severity() { return FindingSeverity.CRITICAL; }
}
