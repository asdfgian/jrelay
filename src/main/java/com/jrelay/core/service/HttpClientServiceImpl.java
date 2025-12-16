package com.jrelay.core.service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import com.jrelay.core.builder.HttpClient;
import com.jrelay.core.builder.RequestBuilder;
import com.jrelay.core.builder.ResponseParser;
import com.jrelay.core.builder.RequestBuilder.RequestBuildResult;
import com.jrelay.core.models.request.Request;
import com.jrelay.core.models.response.Response;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

public final class HttpClientServiceImpl implements HttpClientService {

    public HttpClientServiceImpl() {
    }

    @Override
    public CompletableFuture<Response> sendAsync(Request request) {
        CompletableFuture<Response> future = new CompletableFuture<>();

        RequestBuildResult buildResult = RequestBuilder.build(request);

        if (!buildResult.isSuccess()) {
            future.complete(new Response("Error: " + buildResult.errorMessage()));
            return future;
        }

        OkHttpClient baseClient = HttpClient.getInstance();
        OkHttpClient clientToUse = baseClient;

        long start = System.nanoTime();

        clientToUse.newCall(buildResult.request()).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.complete(new Response("Connection error: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, okhttp3.@NotNull Response response) {
                try {
                    long duration = (System.nanoTime() - start) / 1_000_000;

                    Response parsedResponse = ResponseParser.parse(response, duration);

                    future.complete(parsedResponse);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    future.complete(new Response("Response parsing error: " + ex.getMessage()));
                }
            }
        });

        return future;
    }

}
