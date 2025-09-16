package com.jrelay.core.models.request;

/**
 * Represents a single HTTP header with a key-value pair.
 *
 * <p>
 * This record is used to define custom HTTP headers in a request.
 * Common examples include headers like "Content-Type", "Authorization", etc.
 *
 * @param key   the name of the header
 * @param value the value associated with the header key
 * 
 * @author @ASDG14N
 * @since 05-08-2025
 */

public record HttpHeader(boolean selected, String key, String value) {
}
