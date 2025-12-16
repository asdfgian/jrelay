package com.jrelay.core.builder.auth;

public class OAuth2AuthStrategy implements AuthStrategy {

    @Override
    public void applyAuth(okhttp3.Request.Builder requestBuilder,
            com.jrelay.core.models.request.auth.Auth auth) {
        // Future implementation: OAuth 2.0 support
        throw new UnsupportedOperationException("OAuth 2.0 not yet implemented");
    }

    @Override
    public boolean supports(com.jrelay.core.models.request.auth.Auth auth) {
        // Check for OAuth2Auth when Auth interface is extended
        return false;
    }
}
