package com.jrelay.core.models.request.auth;

public record OAuth1Auth(
        AddAuthDataTo addAuthDataTo,
        SignatureMethod signatureMethod,

        String consumerKey,
        String consumerSecret,
        String accessToken,
        String tokenSecret,

        String callbackUrl,
        String verifier,
        String timestamp,
        String nonce,
        String version,
        String realm,

        boolean includeBodyHash,
        boolean addEmptyParamsToSignature) implements Auth {

    public OAuth1Auth(
            int addAuthDataTo,
            int signatureMethod,

            String consumerKey,
            String consumerSecret,
            String accessToken,
            String tokenSecret,

            String callbackUrl,
            String verifier,
            String timestamp,
            String nonce,
            String version,
            String realm,

            boolean includeBodyHash,
            boolean addEmptyParamsToSignature) {
        this(AddAuthDataTo.values()[addAuthDataTo], SignatureMethod.values()[signatureMethod],
                consumerKey, consumerSecret, accessToken, tokenSecret,
                callbackUrl, verifier, timestamp, nonce, version, realm,
                includeBodyHash, addEmptyParamsToSignature);
    }

    public enum AddAuthDataTo {
        AUTO,
        REQUEST_BODY,
        REQUEST_URL,
        REQUEST_HEADERS
    }

    public enum SignatureMethod {
        HMAC_SHA1,
        HMAC_SHA256,
        HMAC_SHA512,
        RSA_SHA1,
        RSA_SHA256,
        RSA_SHA512,
        PLAINTEXT
    }

}
