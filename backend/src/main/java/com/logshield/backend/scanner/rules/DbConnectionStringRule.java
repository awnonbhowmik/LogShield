package com.logshield.backend.scanner.rules;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class DbConnectionStringRule extends AbstractRegexRule {

    // Matches connection URIs for PostgreSQL, MySQL, MongoDB, Redis, MSSQL, Oracle
    // e.g. postgresql://user:pass@host:5432/db  or  mongodb+srv://admin:secret@cluster.example.net/
    private static final Pattern PATTERN = Pattern.compile(
            "(?i)\\b(?:postgresql|postgres|mysql|mariadb|mongodb(?:\\+srv)?|redis|mssql|sqlserver|oracle)" +
            "://[^\\s\"'<>]+"
    );

    @Override protected Pattern pattern()       { return PATTERN; }
    @Override protected String placeholder()    { return "[REDACTED_DB_URL]"; }
    @Override public FindingCategory category() { return FindingCategory.DB_CONNECTION_STRING; }
    @Override public FindingSeverity severity()  { return FindingSeverity.CRITICAL; }
}
