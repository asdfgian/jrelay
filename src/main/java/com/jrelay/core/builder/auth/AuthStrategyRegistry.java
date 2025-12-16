package com.jrelay.core.builder.auth;

import java.util.ArrayList;
import java.util.List;

import com.jrelay.core.models.request.auth.Auth;

import okhttp3.Request;

public class AuthStrategyRegistry {

    private static final List<AuthStrategy> strategies = new ArrayList<>();

    static {
        strategies.add(new OAuth1AuthStrategy());
        // OAuth2AuthStrategy can be added here when implemented
    }

    private AuthStrategyRegistry() {
    }

    public static boolean apply(Request.Builder requestBuilder, Auth auth) {
        for (AuthStrategy strategy : strategies) {
            if (strategy.supports(auth)) {
                strategy.applyAuth(requestBuilder, auth);
                return true;
            }
        }
        return false;
    }

    public static void register(AuthStrategy strategy) {
        strategies.add(0, strategy);
    }
}
