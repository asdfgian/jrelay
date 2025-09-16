package com.jrelay.core.models.request;

/**
 * Represents a single query parameter in an HTTP request URL.
 *
 * <p>
 * A query parameter is typically part of the URL in the format
 * {@code ?key=value},
 * used to send data to the server as part of the request URL.
 *
 * @param key   the name of the query parameter
 * @param value the value associated with the query parameter
 * 
 * @author @ASDG14N
 * @since 05-08-2025
 */

public record QueryParameter(boolean selected, String key, String value) {
}
