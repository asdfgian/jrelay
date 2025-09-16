package com.jrelay.core.models.request.body;

public record BinaryBody(String filePath) implements Body {

    @Override
    public String content() {
        return filePath;
    }

    @Override
    public String contentType() {
        return "application/octet-stream";
    }
}