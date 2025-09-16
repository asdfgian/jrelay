package com.jrelay.core.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;

public final class StringUtils {

    private StringUtils() {
    }

    public static String replaceVariables(String input, Map<String, String> replacements) {
        StringSubstitutor substitutor = new StringSubstitutor(replacements, "{{", "}}");
        return substitutor.replace(input);
    }

    public static String insertVariable(String text, int caretPos, String variable) {
        int lastIndex = text.lastIndexOf("{{", caretPos);
        if (lastIndex == -1) {
            return text;
        }
        String before = text.substring(0, lastIndex);
        String after = text.substring(caretPos);
        return before + "{{" + variable + "}}" + after;
    }

    public static List<int[]> findVariableRanges(String text) {
        List<int[]> ranges = new ArrayList<>();
        int start = 0;
        while ((start = text.indexOf("{{", start)) != -1) {
            int end = text.indexOf("}}", start);
            if (end != -1) {
                ranges.add(new int[] { start, end + 2 });
                start = end + 2;
            } else {
                break;
            }
        }
        return ranges;
    }

    public static String buildUrl(String baseUrl, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }
        String queryString = params.entrySet().stream()
                .filter(e -> e.getKey() != null && !e.getKey().isBlank())
                .map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
                .collect(Collectors.joining("&"));

        return baseUrl + (queryString.isEmpty() ? "" : "?" + queryString);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public static String escapeDoubleQuotes(String input) {
        return StringEscapeUtils.escapeJava(input);
    }

    public static String escapeSingleQuotes(String input) {
        return input.replace("'", "'\\''");
    }

    /**
     * Extracts the base URL from a full URL string by removing any query
     * parameters.
     * <p>
     * If the URL contains a {@code ?} character, the substring before it is
     * returned.
     * Otherwise, the original URL is returned unchanged.
     *
     * @param fullUrl the complete URL, possibly containing query parameters
     * @return the base URL without query parameters
     * 
     * @author ASDFG14N
     * @since 06-08-2025
     */
    public static String extractBaseUrl(String fullUrl) {
        int idx = fullUrl.indexOf("?");
        return (idx != -1) ? fullUrl.substring(0, idx) : fullUrl;
    }

    public static String capitalize(String arg0) {
        if (arg0 == null || arg0.isEmpty()) {
            return arg0;
        }
        return arg0.substring(0, 1).toUpperCase() + arg0.substring(1).toLowerCase();
    }

}
