package com.jrelay.core.service;

import java.util.concurrent.CompletableFuture;

import com.jrelay.core.models.request.Request;
import com.jrelay.core.models.response.Response;

public interface HttpClientService {
    CompletableFuture<Response> sendAsync(Request request);
}
