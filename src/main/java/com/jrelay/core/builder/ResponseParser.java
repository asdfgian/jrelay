package com.jrelay.core.builder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.InflaterInputStream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.jrelay.core.models.response.ContentDisplayType;

public class ResponseParser {

    private ResponseParser() {
    }

    public static com.jrelay.core.models.response.Response parse(okhttp3.Response res, long duration) {
        Map<String, List<String>> headers = res.headers().names().stream()
                .collect(Collectors.toMap(name -> name, res::headers, (a, b) -> b));

        String contentType = headers.entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase("Content-Type"))
                .findFirst()
                .map(e -> e.getValue().isEmpty() ? null : e.getValue().getFirst())
                .orElse("application/octet-stream");

        String contentEncoding = headers.entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase("Content-Encoding"))
                .findFirst()
                .map(e -> e.getValue().isEmpty() ? null : e.getValue().getFirst())
                .orElse(null);

        ContentDisplayType displayType = ContentDisplayType.detect(contentType);

        try {
            byte[] rawBytes = res.body() != null ? res.body().bytes() : null;

            if (rawBytes != null && "deflate".equalsIgnoreCase(contentEncoding)) {
                try (InflaterInputStream inflater = new InflaterInputStream(new ByteArrayInputStream(rawBytes))) {
                    rawBytes = inflater.readAllBytes();
                }
            }

            switch (displayType) {
                case JSON, XML, YAML, HTML, TEXT, CSV, FORM_URL_ENCODED -> {
                    String body = rawBytes != null ? new String(rawBytes) : null;
                    long size = body != null ? body.getBytes().length : 0;
                    return new com.jrelay.core.models.response.Response(
                            res.code(),
                            Duration.format(duration),
                            Size.format(size),
                            headers,
                            Body.format(body),
                            displayType);
                }
                default -> {
                    long size = rawBytes != null ? rawBytes.length : 0;
                    return new com.jrelay.core.models.response.Response(
                            res.code(),
                            Duration.format(duration),
                            Size.format(size),
                            headers,
                            rawBytes,
                            displayType);
                }
            }
        } catch (IOException e) {
            return new com.jrelay.core.models.response.Response(
                    "[Error reading body: " + e.getMessage() + "]");
        }
    }

    private static class Size {
        public static String format(long bytes) {
            if (bytes < 1024)
                return bytes + " B";
            int exp = (int) (Math.log(bytes) / Math.log(1024));
            String unit = "KMGTPE".charAt(exp - 1) + "B";
            return String.format("%.2f %s", bytes / Math.pow(1024, exp), unit);
        }
    }

    private static class Duration {

        public static String format(long millis) {
            if (millis < 1000) {
                return millis + " ms";
            } else if (millis < 60_000) {
                return String.format("%.2f s", millis / 1000.0);
            } else if (millis < 3_600_000) {
                long minutes = millis / 60_000;
                long seconds = (millis % 60_000) / 1000;
                return minutes + " min " + seconds + " s";
            } else {
                long hours = millis / 3_600_000;
                long minutes = (millis % 3_600_000) / 60_000;
                return hours + " h " + minutes + " min";
            }
        }
    }

    private static class Body {

        private static final ObjectMapper objectMapper = new ObjectMapper();
        private static final ObjectWriter prettyPrinter = objectMapper.writer(new CustomPrettyPrinter());

        private static String format(String json) {
            try {
                Object obj = objectMapper.readValue(CustomPrettyPrinter.minify(json), Object.class);
                return prettyPrinter.writeValueAsString(obj);
            } catch (Exception e) {
                return json;
            }
        }
    }

    public static class CustomPrettyPrinter extends DefaultPrettyPrinter {
        private static final DefaultIndenter INDENTER =
                new DefaultIndenter("  ", System.lineSeparator());

        public CustomPrettyPrinter() {
            this._arrayIndenter = INDENTER;
            this._objectIndenter = INDENTER;
        }

        @Override
        public DefaultPrettyPrinter createInstance() {
            return new CustomPrettyPrinter();
        }

        @Override
        public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
            g.writeRaw(',');
            g.writeRaw(System.lineSeparator());
            if (_nesting > 0) {
                _arrayIndenter.writeIndentation(g, _nesting);
            }
        }

        private static String minify(String json) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            try {
                return objectMapper.writeValueAsString(jsonNode);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
