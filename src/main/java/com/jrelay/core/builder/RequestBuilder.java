package com.jrelay.core.builder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import com.jrelay.core.builder.auth.AuthStrategyRegistry;
import com.jrelay.core.models.request.HttpHeader;
import com.jrelay.core.models.request.Method;
import com.jrelay.core.models.request.auth.Auth;
import com.jrelay.core.models.request.auth.BasicAuth;
import com.jrelay.core.models.request.auth.BearerTokenAuth;
import com.jrelay.core.models.request.body.BinaryBody;
import com.jrelay.core.models.request.body.Body;
import com.jrelay.core.models.request.body.FormDataBody;
import com.jrelay.core.models.request.body.FormEncodeBody;
import com.jrelay.core.models.request.body.FormDataBody.FormDataPart;
import com.jrelay.core.models.request.body.FormDataBody.FormDataPart.PartType;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.shared.utils.UiUtils;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestBuilder {

    private static final RequestBody NO_BODY = RequestBody.create(new byte[0]);

    public static RequestBuildResult build(com.jrelay.core.models.request.Request request) {
        try {
            RequestBuilder.validateUrl(request.getUrl());

            Request.Builder builder = new Request.Builder().url(request.getUrl());

            RequestBuilder.addDefaultHeaders(builder);

            RequestBuilder.addCustomHeaders(builder, request.getHeaders());

            if (request.getAuth() instanceof BasicAuth || request.getAuth() instanceof BearerTokenAuth) {
                RequestBuilder.addAuthHeaders(builder, request.getAuth());
            }

            RequestBody requestBody = buildRequestBody(request.getBody());
            RequestBuilder.applyMethod(builder, request.getMethod(), requestBody);

            if (request.getAuth() != null
                    && !(request.getAuth() instanceof BasicAuth)
                    && !(request.getAuth() instanceof BearerTokenAuth)) {
                AuthStrategyRegistry.apply(builder, request.getAuth());
            }

            return new RequestBuildResult(builder.build());

        } catch (Exception e) {
            e.printStackTrace();
            return new RequestBuildResult(e.getMessage());
        }
    }

    private static void addDefaultHeaders(Request.Builder builder) {
        builder.addHeader("Cache-Control", "no-cache");
        builder.addHeader("User-Agent", UiUtils.APP_NAME + "/" + UiUtils.APP_VERSION);
        builder.addHeader("Accept", "*/*");
        builder.addHeader("Connection", "keep-alive");
    }

    private static void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException(LangManager.text("model.requestBuilder.validateUrl.error"));
        }
    }

    private static void addCustomHeaders(Request.Builder builder, List<HttpHeader> headers) {
        if (headers != null) {
            headers.forEach(header -> builder.header(header.key(), header.value()));
        }
    }

    private static void addAuthHeaders(Request.Builder builder, Auth auth) {
        Map<String, String> authHeaders = AuthHeaderBuilder.build(auth);
        authHeaders.forEach(builder::addHeader);
    }

    private static RequestBody buildRequestBody(Body body) throws IOException {
        if (body == null)
            return NO_BODY;

        if (!body.content().isEmpty()) {
            return RequestBody.create(body.content(), MediaType.parse(body.contentType()));
        }

        switch (body) {
            case

                    FormEncodeBody(List<FormEncodeBody.FormEncodePart> forms) -> {
                FormBody.Builder formBuilder = new FormBody.Builder();
                forms.forEach(part -> formBuilder.add(part.name(), part.value()));
                return formBuilder.build();
            }
            case

                    FormDataBody(List<FormDataPart> parts) -> {
                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                for (FormDataPart part : parts) {
                    if (part.type() == PartType.TEXT) {
                        multipartBuilder.addFormDataPart(part.name(), part.value());
                    } else if (part.type() == PartType.FILE) {
                        File file = new File(part.value());
                        MediaType mediaType = MediaType.parse(Files.probeContentType(file.toPath()));
                        multipartBuilder.addFormDataPart(part.name(), file.getName(),
                                RequestBody.create(file,
                                        mediaType != null ? mediaType : MediaType.parse("application/octet-stream")));
                    }
                }
                return multipartBuilder.build();
            }
            case BinaryBody binaryBody -> {
                File file = new File(binaryBody.filePath());
                MediaType mediaType = MediaType.parse(Files.probeContentType(file.toPath()));
                return RequestBody.create(file,
                        mediaType != null ? mediaType : MediaType.parse(binaryBody.contentType()));
            }
            default -> {
                //
            }
        }

        return NO_BODY;

    }

    private static void applyMethod(Request.Builder builder, Method method, RequestBody body) {
        RequestBody safeBody = body != null ? body : NO_BODY;
        switch (method) {
            case GET -> builder.get();
            case DELETE -> builder.delete(safeBody);
            case POST -> builder.post(safeBody);
            case PUT -> builder.put(safeBody);
            case PATCH -> builder.patch(safeBody);
            case OPTIONS -> builder.method("OPTIONS", safeBody);
            default -> throw new IllegalArgumentException("HTTP METHOD Not supported: " + method);
        }
    }

    public record RequestBuildResult(
            Request request,
            String errorMessage) {

        public RequestBuildResult(Request request) {
            this(request, null);
        }

        public RequestBuildResult(String errorMessage) {
            this(null, errorMessage);
        }

        public boolean isSuccess() {
            return request != null && errorMessage == null;
        }
    }

}
