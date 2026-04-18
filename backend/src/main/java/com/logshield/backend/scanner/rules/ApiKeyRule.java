package com.logshield.backend.scanner.rules;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ApiKeyRule extends AbstractRegexRule {

    // Matches:
    //   1. Common prefixed keys  e.g. sk-abc123..., api_key-XYZ...
    //   2. AWS IAM access key IDs  e.g. AKIAIOSFODNN7EXAMPLE
    //   3. 32-64 char hex strings (MD5 / SHA hashes used as secrets)
    private static final Pattern PATTERN = Pattern.compile(
            "\\b(?:" +
            "(?:sk|pk|api|key|token|secret|access|bearer|auth)[_\\-][A-Za-z0-9_\\-]{16,}" +
            "|AKIA[0-9A-Z]{16}" +
            "|[0-9a-fA-F]{32,64}" +
            ")\\b"
    );

    @Override protected Pattern pattern()      { return PATTERN; }
    @Override protected String placeholder()   { return "[REDACTED_API_KEY]"; }
    @Override public FindingCategory category() { return FindingCategory.API_KEY; }
    @Override public FindingSeverity severity() { return FindingSeverity.CRITICAL; }
}
