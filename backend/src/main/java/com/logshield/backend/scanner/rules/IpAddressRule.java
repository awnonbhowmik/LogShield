package com.logshield.backend.scanner.rules;

import com.logshield.backend.entity.ScanFinding.FindingCategory;
import com.logshield.backend.entity.ScanFinding.FindingSeverity;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class IpAddressRule extends AbstractRegexRule {

    // IPv4: strict octet validation 0-255
    private static final String IPV4 =
            "(?:(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)";

    // IPv6 hex group
    private static final String H = "[0-9A-Fa-f]{1,4}";

    // Full 8-group IPv6, compressed forms (::), and loopback ::1
    private static final String IPV6 =
            "(?:" +
            "(?:" + H + ":){7}" + H +                    // full
            "|(?:" + H + ":){1,7}:" +                    // trailing ::
            "|:(?::" + H + "){1,7}" +                    // leading ::
            "|(?:" + H + ":){1,6}:" + H +
            "|(?:" + H + ":){1,5}(?::" + H + "){1,2}" +
            "|(?:" + H + ":){1,4}(?::" + H + "){1,3}" +
            "|(?:" + H + ":){1,3}(?::" + H + "){1,4}" +
            "|(?:" + H + ":){1,2}(?::" + H + "){1,5}" +
            "|" + H + ":(?::" + H + "){1,6}" +
            "|::(?:ffff(?::0{1,4})?:)?" + IPV4 +         // IPv4-mapped
            "|(?:" + H + ":){1,4}:" + IPV4 +             // IPv4-compatible
            "|::" +                                       // unspecified
            ")";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<![\\w.])(?:" + IPV4 + "|" + IPV6 + ")(?![\\w.])"
    );

    @Override protected Pattern pattern()      { return PATTERN; }
    @Override protected String placeholder()   { return "[REDACTED_IP]"; }
    @Override public FindingCategory category() { return FindingCategory.IP_ADDRESS; }
    @Override public FindingSeverity severity() { return FindingSeverity.LOW; }
}
