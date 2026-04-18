package com.logshield.backend.scanner;

import java.util.List;

/**
 * Strategy interface for computing an overall severity score (0–100) from a list of findings.
 * Swap the default implementation for a different scoring model without touching the engine.
 */
public interface SeverityScorer {

    int score(List<RawMatch> matches);
}
