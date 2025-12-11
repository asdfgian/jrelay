package com.jrelay.core.models.response;

import java.util.List;
import java.util.Map;

import com.jrelay.core.utils.StringUtils;

/**
 * Represents an HTTP response, containing status, metadata, headers, and body
 * data
 * in either text or binary form, along with error information if applicable.
 * <p>
 * Provides multiple constructors to create a response with text content, binary
 * content,
 * or an error message.
 *
 * @param status       the HTTP status code of the response
 * @param duration     the time taken to receive the response, as a string
 * @param size         the size of the response, as a string
 * @param headers      a map of header names to their associated list of values
 * @param body         the response body as text (if applicable)
 * @param bodyBytes    the response body as binary data (if applicable)
 * @param hasError     {@code true} if the response represents an error;
 *                     {@code false} otherwise
 * @param errorMessage the error message if {@code hasError} is {@code true},
 *                     otherwise {@code null}
 * @author ASDFG14N
 * @since 14-08-2025
 */
public record Response(
        Integer status,
        String duration,
        String size,
        Map<String, List<String>> headers,
        String body,
        byte[] bodyBytes,
        boolean hasError,
        String errorMessage,
        ContentDisplayType displayType) {

    /**
     * Creates a {@link Response} instance containing text-based body content.
     * <p>
     * Sets the binary body to {@code null} and marks the response as non-error by
     * default.
     *
     * @param status   the HTTP status code of the response
     * @param duration the time taken to receive the response, as a string
     * @param size     the size of the response, as a string
     * @param headers  a map of header names to their associated list of values
     * @param bodyText the response body as text
     * @author ASDFG14N
     * @since 14-08-2025
     */
    public Response(Integer status, String duration, String size,
                    Map<String, List<String>> headers, String bodyText, ContentDisplayType displayType) {
        this(status, duration, size, headers, bodyText, null, false, null, displayType);
    }

    /**
     * Creates a {@link Response} instance containing binary body content.
     * <p>
     * Sets the text body to {@code null} and marks the response as non-error by
     * default.
     *
     * @param status    the HTTP status code of the response
     * @param duration  the time taken to receive the response, as a string
     * @param size      the size of the response, as a string
     * @param headers   a map of header names to their associated list of values
     * @param bodyBytes the response body as binary data
     * @author ASDFG14N
     * @since 14-08-2025
     */
    public Response(Integer status, String duration, String size,
                    Map<String, List<String>> headers, byte[] bodyBytes, ContentDisplayType displayType) {
        this(status, duration, size, headers, null, bodyBytes, false, null, displayType);
    }

    /**
     * Creates a {@link Response} instance representing an error state.
     * <p>
     * Sets status, duration, and size to default empty values, clears both text and
     * binary body content,
     * and marks the response as an error with the provided message.
     *
     * @param errorMessage the error message describing the failure
     * @author ASDFG14N
     * @since 14-08-2025
     */
    public Response(String errorMessage) {
        this(0, "", "", Map.of(), null, null, true, errorMessage, null);
    }

    /**
     * Checks whether the response contains text-based body content.
     *
     * @return {@code true} if the text body is not {@code null}; {@code false}
     * otherwise
     * @author ASDFG14N
     * @since 14-08-2025
     */
    public boolean isText() {
        return body != null;
    }

    /**
     * Checks whether the response contains binary body content.
     *
     * @return {@code true} if the binary body is not {@code null}; {@code false}
     * otherwise
     * @author ASDFG14N
     * @since 14-08-2025
     */
    public boolean isBinary() {
        return bodyBytes != null;
    }

    public String headersToString() {
        StringBuilder sb = new StringBuilder();
        headers.entrySet().stream()
                .filter(entry -> entry.getKey() != null)
                .sorted(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))
                .forEach(entry -> {
                    String key = StringUtils.capitalize(entry.getKey());
                    for (String value : entry.getValue()) {
                        sb.append(key)
                                .append(": ").append('"').append(value).append('"')
                                .append("\n");
                    }
                });

        return sb.toString();
    }
}
