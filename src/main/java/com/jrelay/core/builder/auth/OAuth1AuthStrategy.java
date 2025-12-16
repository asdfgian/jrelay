package com.jrelay.core.builder.auth;

import okhttp3.Request;

import java.util.Map;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.jrelay.core.models.request.auth.Auth;
import com.jrelay.core.models.request.auth.OAuth1Auth;

public class OAuth1AuthStrategy implements AuthStrategy {

    @Override
    public void applyAuth(Request.Builder requestBuilder, Auth auth) {
        if (!(auth instanceof OAuth1Auth oauth1)) {
            throw new IllegalArgumentException("OAuth1AuthStrategy expects OAuth1Auth");
        }

        try {
            Request currentRequest = requestBuilder.build();

            OAuth10aService service = new ServiceBuilder(oauth1.consumerKey())
                    .apiSecret(oauth1.consumerSecret())
                    .build(GenericOAuth1Api.instance());

            String tokenStr = oauth1.accessToken() != null ? oauth1.accessToken() : "";
            String secretStr = oauth1.tokenSecret() != null ? oauth1.tokenSecret() : "";
            OAuth1AccessToken accessToken = new OAuth1AccessToken(tokenStr, secretStr);

            Verb verb = Verb.valueOf(currentRequest.method().toUpperCase());
            OAuthRequest scribeRequest = new OAuthRequest(verb, currentRequest.url().toString());

            // 4. Firmar la petición
            // HMAC-SHA1
            service.signRequest(accessToken, scribeRequest);

            Map<String, String> headers = scribeRequest.getHeaders();
            String authHeader = headers.get("Authorization");

            if (authHeader != null) {
                requestBuilder.removeHeader("Authorization");
                requestBuilder.addHeader("Authorization", authHeader);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to apply OAuth 1.0a signature via ScribeJava", e);
        }
    }

    @Override
    public boolean supports(Auth auth) {
        return auth instanceof OAuth1Auth;
    }

    private static class GenericOAuth1Api extends DefaultApi10a {
        private static final GenericOAuth1Api INSTANCE = new GenericOAuth1Api();

        private GenericOAuth1Api() {
        }

        public static GenericOAuth1Api instance() {
            return INSTANCE;
        }

        @Override
        public String getAccessTokenEndpoint() {
            return "http://unused.com/access_token";
        }

        @Override
        public String getRequestTokenEndpoint() {
            return "http://unused.com/request_token";
        }

        @Override
        public String getAuthorizationUrl(com.github.scribejava.core.model.OAuth1RequestToken requestToken) {
            return "http://unused.com/authorize";
        }

        @Override
        protected String getAuthorizationBaseUrl() {
            return "http://unused.com/authorize";
        }
    }
}