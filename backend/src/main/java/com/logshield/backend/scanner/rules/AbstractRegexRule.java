package com.logshield.backend.scanner.rules;

import com.logshield.backend.scanner.DetectionRule;
import com.logshield.backend.scanner.RawMatch;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class for regex-backed rules.
 * Subclasses only need to supply pattern(), placeholder(), category(), and severity().
 * To add a new rule: extend this class, annotate with @Component, and it is auto-registered.
 */
public abstract class AbstractRegexRule implements DetectionRule {

    protected abstract Pattern pattern();

    protected abstract String placeholder();

    @Override
    public List<RawMatch> detect(String text) {
        List<RawMatch> matches = new ArrayList<>();
        Matcher m = pattern().matcher(text);
        while (m.find()) {
            matches.add(new RawMatch(
                    category(),
                    severity(),
                    m.group(),
                    placeholder(),
                    lineOf(text, m.start()),
                    m.start(),
                    m.end()
            ));
        }
        return matches;
    }

    /** Counts newlines before the given character offset to derive a 1-based line number. */
    static int lineOf(String text, int offset) {
        int line = 1;
        for (int i = 0; i < offset; i++) {
            if (text.charAt(i) == '\n') line++;
        }
        return line;
    }
}
