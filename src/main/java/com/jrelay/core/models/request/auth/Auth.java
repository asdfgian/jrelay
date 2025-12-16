package com.jrelay.core.models.request.auth;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BasicAuth.class, name = "basic"),
        @JsonSubTypes.Type(value = BearerTokenAuth.class, name = "bearer"),
        @JsonSubTypes.Type(value = OAuth1Auth.class, name = "oauth1")
})
public sealed interface Auth permits BasicAuth, BearerTokenAuth, OAuth1Auth {
}
