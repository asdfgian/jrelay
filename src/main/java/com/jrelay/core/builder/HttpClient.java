package com.jrelay.core.builder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.brotli.BrotliInterceptor;

/**
 * Singleton provider for a configured {@link OkHttpClient} instance.
 * <p>
 * Lazily initializes the HTTP client with predefined connection, read,
 * and write timeouts to ensure consistent network request behavior
 * across the application.
 *
 * @author ASDFG14N
 * @since 14-08-2025
 */

public class HttpClient {
    
    private HttpClient() {
    }

    private static OkHttpClient instance;

    /**
     * Returns the singleton {@link OkHttpClient} instance.
     * <p>
     * If the instance has not yet been created, it is initialized with:
     * <ul>
     * <li>Connection timeout: 15 seconds</li>
     * <li>Read timeout: 30 seconds</li>
     * <li>Write timeout: 20 seconds</li>
     * </ul>
     *
     * @return the configured {@link OkHttpClient} singleton instance
     */

    public static OkHttpClient getInstance() {
        if (instance == null) {
            instance = new OkHttpClient.Builder()
                    .addInterceptor(BrotliInterceptor.INSTANCE)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .build();
        }
        return instance;
    }
}
