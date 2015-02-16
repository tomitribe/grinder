/*
 * Tomitribe Confidential
 *
 * Copyright Tomitribe Corporation. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.tomitribe.performance.scriptengine.crest;


import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Expand {
    private static final Pattern EXPANSION_PATTERN =
            Pattern.compile("(\\$\\{([^}]+?)\\})", Pattern.MULTILINE);

    private Expand() {
    }

    public static String expand(final String input, final Properties properties) {
        if (input == null) {
            return null;
        }

        Matcher matcher = EXPANSION_PATTERN.matcher(input);

        StringBuffer expanded = new StringBuffer(input.length());
        while (matcher.find()) {
            String propName = matcher.group(2);
            String value = (String) properties.get(propName);
            if (value == null) {
                value = matcher.group(0);
            }
            matcher.appendReplacement(expanded, "");
            expanded.append(value);
        }

        matcher.appendTail(expanded);
        return expanded.toString();
    }
}
