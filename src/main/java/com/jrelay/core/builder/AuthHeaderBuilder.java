package com.jrelay.core.builder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import com.jrelay.core.models.request.auth.Auth;
import com.jrelay.core.models.request.auth.BasicAuth;
import com.jrelay.core.models.request.auth.BearerTokenAuth;

/**
 * Utility class for constructing HTTP Authorization headers based on the
 * authentication type.
 *
 * <p>
 * This class supports:
 * <ul>
 * <li>{@link BasicAuth} - Produces a Basic Authorization header with
 * Base64-encoded credentials.</li>
 * <li>{@link BearerTokenAuth} - Produces a Bearer token Authorization
 * header.</li>
 * </ul>
 *
 * <p>
 * If an unsupported authentication type is passed, an
 * {@link IllegalArgumentException} is thrown.
 *
 * @author @ASDG14N
 * @since 05-08-2025
 */
public class AuthHeaderBuilder {

    private AuthHeaderBuilder() {
    }

    /**
     * Builds an Authorization header map based on the provided authentication
     * instance.
     *
     * @param auth the authentication object (e.g. {@link BasicAuth} or
     *             {@link BearerTokenAuth})
     * @return a map containing the Authorization header key and its corresponding
     *         value
     */
    public static Map<String, String> build(Auth auth) {
        if (auth instanceof BasicAuth(String username, String password)) {
            String credentials = username + ":" + password;
            String encoded = Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
            return Map.of("Authorization", "Basic " + encoded);
        } else if (auth instanceof BearerTokenAuth(String token)) {
            return Map.of("Authorization", token);
        } else {
            return Collections.emptyMap();
        }
    }
}
