package com.jrelay.core.models.request.body;

import java.util.List;

public record FormEncodeBody(List<FormEncodePart> forms) implements Body {

    public record FormEncodePart(String name, String value) {
    }

    @Override
    public String content() {
        return "";
    }

    @Override
    public String contentType() {
        return "application/x-www-form-urlencoded";
    }
}
