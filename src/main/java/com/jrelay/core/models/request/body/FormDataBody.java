package com.jrelay.core.models.request.body;

import java.util.List;

public record FormDataBody(List<FormDataPart> parts) implements Body {

    @Override
    public String content() {
        return "";
    }

    @Override
    public String contentType() {
        return "multipart/form-data";
    }

    public record FormDataPart(String name, PartType type, String value) {
        public enum PartType {
            TEXT, FILE
        }
    }
}
