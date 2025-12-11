package com.jrelay.core.models.response;

import lombok.Getter;

public enum ContentDisplayType {
    // Text
    JSON("application/json", "application/*+json"),
    XML("application/xml", "text/xml", "application/*+xml"),
    YAML("application/x-yaml", "text/yaml"),
    HTML("text/html"),
    TEXT("text/plain"),
    FORM_URL_ENCODED("application/x-www-form-urlencoded"),
    MULTIPART("multipart/form-data"),
    CSV("text/csv"),

    // Binary
    IMAGE("image/png", "image/jpeg", "image/gif", "image/webp", "image/svg+xml"),
    PDF("application/pdf"),
    AUDIO("audio/mpeg", "audio/wav", "audio/ogg"),
    VIDEO("video/mp4", "video/webm", "video/ogg"),
    ARCHIVE("application/zip", "application/x-rar-compressed", "application/x-7z-compressed"),
    BINARY("application/octet-stream");

    @Getter
    private final String[] mimeTypes;

    ContentDisplayType(String... mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public static ContentDisplayType detect(String contentType) {
        if (contentType == null) {
            return BINARY;
        }

        String cleanType = contentType.split(";")[0].trim().toLowerCase();

        for (ContentDisplayType type : values()) {
            if (matchesAnyMime(type, cleanType)) {
                return type;
            }
        }

        return BINARY;
    }

    private static boolean matchesAnyMime(ContentDisplayType type, String cleanType) {
        for (String mime : type.mimeTypes) {
            if (matchesMime(mime, cleanType)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesMime(String mime, String cleanType) {
        if (mime.endsWith("*")) {
            return cleanType.startsWith(mime.substring(0, mime.length() - 1));
        }
        if (mime.contains("*")) {
            String regex = mime.replace("*", ".*");
            return cleanType.matches(regex);
        }
        return cleanType.equalsIgnoreCase(mime);
    }

}
