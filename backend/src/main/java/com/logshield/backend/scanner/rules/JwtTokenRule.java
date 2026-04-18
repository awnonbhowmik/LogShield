package com.logshield.backend.scanner.rules;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class JwtTokenRule extends AbstractRegexRule {

    // JWTs always start with eyJ (base64 of {"  ), three dot-separated base64url segments
    private static final Pattern PATTERN = Pattern.compile(
            "eyJ[A-Za-z0-9_\\-]+\\.eyJ[A-Za-z0-9_\\-]+\\.[A-Za-z0-9_\\-]+"
    );

    @Override protected Pattern pattern()      { return PATTERN; }
    @Override protected String placeholder()   { return "[REDACTED_JWT]"; }
    @Override public FindingCategory category() { return FindingCategory.JWT_TOKEN; }
    @Override public FindingSeverity severity() { return FindingSeverity.HIGH; }
}
