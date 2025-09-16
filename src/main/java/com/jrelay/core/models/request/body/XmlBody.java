package com.jrelay.core.models.request.body;

/**
 * Represents an HTTP request body containing XML content.
 *
 * <p>
 * This implementation wraps an XML-formatted string and exposes
 * it as the body content for HTTP requests, specifying the appropriate
 * MIME type.
 *
 * @param xml the XML content to be used as the request body
 * @author @ASDG14N
 * @since 05-08-2025
 * @see Body
 */

public record XmlBody(String xml) implements Body {
    @Override
    public String content() {
        return xml;
    }

    @Override
    public String contentType() {
        return "application/xml";
    }
}