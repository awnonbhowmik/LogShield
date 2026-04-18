package com.logshield.backend.scanner;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Replaces each matched span in the original text with its placeholder.
 * Processes replacements in reverse start-position order so earlier offsets
 * remain valid as the string grows or shrinks.
 */
@Component
public class RedactionService {

    public String redact(String text, List<RawMatch> matches) {
        StringBuilder sb = new StringBuilder(text);

        matches.stream()
                .sorted(Comparator.comparingInt(RawMatch::start).reversed())
                .forEach(m -> sb.replace(m.start(), m.end(), m.redactedValue()));

        return sb.toString();
    }
}
