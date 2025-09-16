package com.jrelay.core.models;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.jrelay.core.models.request.Request;

public record RequestHistory(String timestamp, Request request) {
    public RequestHistory(Request request) {
        this(formatNow(), request);
    }

    private static String formatNow() {
        ZonedDateTime now = Instant.now().atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm:ss");
        return now.format(formatter);
    }
}
