package com.logshield.backend.scanner;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;

import java.util.List;

/**
 * Contract for a single detection rule.
 * Add a new Spring @Component that implements this interface to register it automatically.
 */
public interface DetectionRule {

    FindingCategory category();

    /** Severity applied to every match produced by this rule. */
    FindingSeverity severity();

    List<RawMatch> detect(String text);
}
