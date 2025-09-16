package com.jrelay.core.models.request.auth;

/**
 * Represents HTTP Basic Authentication credentials.
 *
 * <p>
 * This implementation stores a plain username and password,
 * which are typically encoded in Base64 and used in the {@code Authorization}
 * header
 * of HTTP requests in the format: {@code "Basic <encoded-credentials>"}.
 *
 * @param username the username for basic authentication
 * @param password the password for basic authentication
 *
 * @author @ASDG14N
 * @since 05-08-2025
 * @see Auth
 */

public record BasicAuth(String username, String password) implements Auth {
}