package com.logshield.backend.scanner.rules;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EmailRule extends AbstractRegexRule {

    // RFC-5321-compatible local part + domain
    private static final Pattern PATTERN = Pattern.compile(
            "[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}",
            Pattern.CASE_INSENSITIVE
    );

    @Override protected Pattern pattern()      { return PATTERN; }
    @Override protected String placeholder()   { return "[REDACTED_EMAIL]"; }
    @Override public FindingCategory category() { return FindingCategory.EMAIL; }
    @Override public FindingSeverity severity() { return FindingSeverity.LOW; }
}
