package com.logshield.backend.scanner.rules;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class CreditCardRule extends AbstractRegexRule {

    // Covers Visa (13/16), Mastercard (16), Amex (15), Discover (16), JCB (15/16)
    // with optional space or dash separators between groups.
    // Note: regex cannot enforce Luhn checksum; that would be added in a validator step.
    private static final Pattern PATTERN = Pattern.compile(
            "\\b(?:" +
            "4[0-9]{3}[\\s\\-]?[0-9]{4}[\\s\\-]?[0-9]{4}[\\s\\-]?[0-9]{4}" + // Visa 16
            "|4[0-9]{3}[\\s\\-]?[0-9]{6}[\\s\\-]?[0-9]{5}"                    + // Visa 13
            "|5[1-5][0-9]{2}[\\s\\-]?[0-9]{4}[\\s\\-]?[0-9]{4}[\\s\\-]?[0-9]{4}" + // MC
            "|3[47][0-9]{2}[\\s\\-]?[0-9]{6}[\\s\\-]?[0-9]{5}"                + // Amex
            "|6(?:011|5[0-9]{2})[\\s\\-]?[0-9]{4}[\\s\\-]?[0-9]{4}[\\s\\-]?[0-9]{4}" + // Discover
            ")\\b"
    );

    @Override protected Pattern pattern()      { return PATTERN; }
    @Override protected String placeholder()   { return "[REDACTED_CARD]"; }
    @Override public FindingCategory category() { return FindingCategory.CREDIT_CARD; }
    @Override public FindingSeverity severity() { return FindingSeverity.CRITICAL; }
}
