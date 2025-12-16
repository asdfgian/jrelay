package com.jrelay.core.builder.auth;

import okhttp3.Request;
import com.jrelay.core.models.request.auth.Auth;

public interface AuthStrategy {

    /**
     * Applies authentication to the given OkHttp request builder.
     * 
     * @param requestBuilder the OkHttp request builder to modify
     * @param auth           the authentication credentials
     */
    void applyAuth(Request.Builder requestBuilder, Auth auth);

    /**
     * Determines if this strategy can handle the given authentication type.
     * 
     * @param auth the authentication object
     * @return true if this strategy can process the auth, false otherwise
     */
    boolean supports(Auth auth);
}
