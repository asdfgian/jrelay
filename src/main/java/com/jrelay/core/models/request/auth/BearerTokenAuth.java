package com.jrelay.core.models.request.auth;

/**
 * Represents Bearer Token Authentication credentials.
 *
 * <p>
 * This implementation stores a bearer token, which is typically included
 * in the {@code Authorization} header of HTTP requests as:
 * {@code "Bearer <token>"}.
 *
 * @param token the bearer token used for authentication
 *
 * @author @ASDG14N
 * @since 05-08-2025
 * @see Auth
 */

public record BearerTokenAuth(String token) implements Auth {
}
