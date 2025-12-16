package com.jrelay.core.builder.auth;

import okhttp3.Request;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.exceptions.OAuthSignatureException;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.services.HMACSha1SignatureService;
import com.github.scribejava.core.services.PlaintextSignatureService;
import com.github.scribejava.core.services.SignatureService;
import com.github.scribejava.core.utils.OAuthEncoder;
import com.github.scribejava.core.utils.Preconditions;
import com.jrelay.core.models.request.auth.Auth;
import com.jrelay.core.models.request.auth.OAuth1Auth;
import com.jrelay.core.models.request.auth.OAuth1Auth.SignatureMethod;

public class OAuth1AuthStrategy implements AuthStrategy {

    @Override
    public void applyAuth(Request.Builder requestBuilder, Auth auth) {
        if (!(auth instanceof OAuth1Auth oauth1)) {
            throw new IllegalArgumentException("OAuth1AuthStrategy expects OAuth1Auth");
        }

        try {
            Request currentRequest = requestBuilder.build();

            SignatureService sig = SignatureServiceFactory.from(oauth1.signatureMethod());

            OAuth10aService service = new ServiceBuilder(oauth1.consumerKey())
                    .apiSecret(oauth1.consumerSecret())
                    .build(new ConfigurableOAuth1Api(sig));

            String tokenStr = oauth1.accessToken() != null ? oauth1.accessToken() : "";
            String secretStr = oauth1.tokenSecret() != null ? oauth1.tokenSecret() : "";

            OAuth1AccessToken accessToken = new OAuth1AccessToken(tokenStr, secretStr);

            Verb verb = Verb.valueOf(currentRequest.method().toUpperCase());
            OAuthRequest scribeRequest = new OAuthRequest(verb, currentRequest.url().toString());

            if (oauth1.realm() != null && !oauth1.realm().isBlank()) {
                scribeRequest.setRealm(oauth1.realm());
            }

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

    public static class ConfigurableOAuth1Api extends DefaultApi10a {
        private final SignatureService signatureService;

        public ConfigurableOAuth1Api(SignatureService signatureService) {
            this.signatureService = signatureService;
        }

        @Override
        public SignatureService getSignatureService() {
            return signatureService;
        }

        @Override
        public String getAccessTokenEndpoint() {
            return "unused";
        }

        @Override
        public String getRequestTokenEndpoint() {
            return "unused";
        }

        @Override
        protected String getAuthorizationBaseUrl() {
            return "unused";
        }
    }

    private static class SignatureServiceFactory {
        public static SignatureService from(SignatureMethod method) {
            return switch (method) {
                case HMAC_SHA1 -> new HMACSha1SignatureService();
                case HMAC_SHA256 -> new HmacSha256SignatureService();
                case PLAINTEXT -> new PlaintextSignatureService();
                default -> throw new UnsupportedOperationException(
                        "Signature method not supported by OAuth 1.0: " + method);
            };
        }
    }

    private static class HmacSha256SignatureService implements SignatureService {
        private static final String HMAC_SHA256 = "HmacSHA256";
        private static final Charset UTF8 = StandardCharsets.UTF_8;

        @Override
        public String getSignatureMethod() {
            return "HMAC-SHA256";
        }

        @Override
        public String getSignature(String baseString, String apiSecret, String tokenSecret) {
            try {
                Preconditions.checkEmptyString(baseString, "Base string can't be null or empty");
                Preconditions.checkNotNull(apiSecret, "Api secret can't be null");

                String key = OAuthEncoder.encode(apiSecret) + '&' + OAuthEncoder.encode(tokenSecret);
                return doSign(baseString, key);
            } catch (Exception e) {
                throw new OAuthSignatureException(baseString, e);
            }
        }

        protected String doSign(String toSign, String keyString) throws NoSuchAlgorithmException, InvalidKeyException {
            SecretKeySpec key = new SecretKeySpec(keyString.getBytes(UTF8), HMAC_SHA256);
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(key);
            byte[] rawHmac = mac.doFinal(toSign.getBytes(UTF8));

            return Base64.getEncoder()
                    .encodeToString(rawHmac)
                    .replace("\r", "")
                    .replace("\n", "");
        }
    }
}