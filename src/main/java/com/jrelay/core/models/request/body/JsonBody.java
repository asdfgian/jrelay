package com.jrelay.core.models.request.body;

/**
 * Represents an HTTP request body with JSON content.
 *
 * <p>
 * This implementation wraps a JSON-formatted string and provides
 * methods to access its raw content and content type.
 *
 * @param json the JSON string to be sent as the request body
 * 
 * @author @ASDG14N
 * @since 05-08-2025
 * @see Body
 */
public record JsonBody(String json) implements Body {
    @Override
    public String content() {
        return json;
    }

    @Override
    public String contentType() {
        return "application/json";
    }
}
