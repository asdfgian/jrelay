package com.jrelay.core.models.request.body;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = JsonBody.class, name = "json"),
        @JsonSubTypes.Type(value = PlainTextBody.class, name = "text"),
        @JsonSubTypes.Type(value = XmlBody.class, name = "xml"),
        @JsonSubTypes.Type(value = FormEncodeBody.class, name = "form-encode"),
        @JsonSubTypes.Type(value = FormDataBody.class, name = "form-data"),
        @JsonSubTypes.Type(value = BinaryBody.class, name = "binary")
})
public interface Body {
    String content();

    String contentType();
}
