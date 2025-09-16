package com.jrelay.core.repository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jrelay.core.models.RequestHistory;

public class RequestHistoryRepository extends JsonRepository<List<RequestHistory>> {

    private static final int MAX_ENTRIES = 100;

    private final List<RequestHistory> history;

    public RequestHistoryRepository(String path) {
        super(path, new TypeReference<>() {
        });
        this.history = loadOrDefault(new LinkedList<>());
    }

    public List<RequestHistory> findAll() {
        return Collections.unmodifiableList(history);
    }

    public void add(RequestHistory requestHistory) {
        if (history.size() >= MAX_ENTRIES) {
            history.removeFirst();
        }
        history.add(requestHistory);
        setEntity(history);
    }

    public boolean delete(String timestamp) {
        boolean removed = history.removeIf(r -> r.timestamp().equals(timestamp));
        if (removed) {
            setEntity(history);
        }
        return removed;
    }

    public void clearHistory() {
        history.clear();
        setEntity(history);
    }
}
