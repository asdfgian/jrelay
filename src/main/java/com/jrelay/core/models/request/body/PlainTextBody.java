package com.jrelay.core.models.request.body;

/**
 * Represents an HTTP request body with plain text content.
 *
 * <p>
 * This implementation wraps a plain text string and provides
 * methods to retrieve its raw content and MIME type.
 *
 * @param text the plain text to be sent as the request body
 * @author @ASDG14N
 * @since 05-08-2025
 * @see Body
 */

public record PlainTextBody(String text) implements Body {
    @Override
    public String content() {
        return text;
    }

    @Override
    public String contentType() {
        return "text/plain";
    }
}