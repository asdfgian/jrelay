package com.jrelay.core.models.request;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.jrelay.core.models.request.auth.Auth;
import com.jrelay.core.models.request.auth.BasicAuth;
import com.jrelay.core.models.request.auth.BearerTokenAuth;
import com.jrelay.core.models.request.body.Body;
import com.jrelay.core.utils.StringUtils;
import com.jrelay.core.utils.i18n.LangManager;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class Request {

    @Setter
    private String idCollection;
    private final String idRequest;
    @Setter
    private String name;
    @Setter
    private Method method;
    @Setter
    private String url;
    @Setter
    private List<QueryParameter> params;
    @Setter
    private List<HttpHeader> headers;
    @Setter
    private Auth auth;
    @Setter
    private Body body;

    public Request() {
        this.idRequest = UUID.randomUUID().toString();
        this.name = LangManager.text("model.request.name");
        this.method = Method.GET;
        this.url = "https://httpbin.org/anything";
        this.params = new ArrayList<>();
        this.headers = new ArrayList<>();
        this.auth = null;
        this.body = null;
    }

    public Request(Request other) {
        this.idCollection = other.idCollection;
        this.idRequest = other.idRequest;
        this.name = other.name;
        this.method = other.method;
        this.url = other.url;
        this.params = new ArrayList<>(other.params);
        this.headers = new ArrayList<>(other.headers);
        this.auth = other.auth;
        this.body = other.body;
    }

    public String headersToString() {
        if (headers == null || headers.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        headers.stream()
                .filter(h -> h.key() != null && h.value() != null && h.selected())
                .sorted(Comparator.comparing(HttpHeader::key, String.CASE_INSENSITIVE_ORDER))
                .forEach(h -> sb.append(h.key()).append(": ").append('"').append(h.value()).append('"').append("\n"));

        return sb.toString();
    }

    public String toCurl() {
        StringBuilder sb = new StringBuilder("curl \\\n");

        boolean hasBodyOnGet = method == Method.GET
                && body != null
                && body.content() != null
                && !body.content().isEmpty();

        if (method != Method.GET || hasBodyOnGet) {
            sb.append("  -X ").append(method.name()).append(" \\\n");
        }

        sb.append(buildAuth(auth));
        sb.append(buildHeaders(headers));
        sb.append(buildBody(body, headers));
        sb.append("  \"").append(url).append("\"\n");

        return sb.toString();
    }

    private String buildAuth(Auth auth) {
        if (auth instanceof BasicAuth(String username, String password)) {
            return "  -u \"" + StringUtils.escapeDoubleQuotes(username) + ":" +
                    StringUtils.escapeDoubleQuotes(password) + "\" \\\n";
        } else if (auth instanceof BearerTokenAuth(String token)) {
            return "  -H \"Authorization: " +
                    StringUtils.escapeDoubleQuotes(token) + "\" \\\n";
        }
        return "";
    }

    private String buildHeaders(List<HttpHeader> headers) {
        if (headers == null)
            return "";
        return headers.stream()
                .map(h -> "  -H \"" + StringUtils.escapeDoubleQuotes(h.key()) + ": " +
                        StringUtils.escapeDoubleQuotes(h.value()) + "\" \\\n")
                .collect(Collectors.joining());
    }

    private String buildBody(Body body, List<HttpHeader> headers) {
        if (body == null || body.content() == null || body.content().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("  --data '")
                .append(StringUtils.escapeSingleQuotes(body.content()))
                .append("' \\\n");

        String ct = body.contentType();
        if (ct != null && !headerExists(headers)) {
            sb.append("  -H \"Content-Type: ").append(ct).append("\" \\\n");
        }
        return sb.toString();
    }

    private boolean headerExists(List<HttpHeader> headers) {
        if (headers == null)
            return false;
        return headers.stream().anyMatch(h -> h.key().equalsIgnoreCase("Content-Type"));
    }

}
