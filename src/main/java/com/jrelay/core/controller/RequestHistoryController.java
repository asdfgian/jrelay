package com.jrelay.core.controller;

import java.util.List;

import com.jrelay.core.models.RequestHistory;
import com.jrelay.core.models.request.Request;
import com.jrelay.core.repository.RequestHistoryRepository;
import com.jrelay.ui.views.SideBar;

import lombok.Setter;

public class RequestHistoryController {
    @Setter
    private SideBar sideBar;
    private final RequestHistoryRepository repository;

    public RequestHistoryController() {
        this.repository = new RequestHistoryRepository("history.json");
    }

    public List<RequestHistory> findAll() {
        return repository.findAll();
    }

    public void add(Request request) {
        repository.add(new RequestHistory(request));
        sideBar.getHttpPanel().getTreePanelHistory().renderNodes();
    }

    public void delete(String timestamp) {
        repository.delete(timestamp);
        sideBar.getHttpPanel().getTreePanelHistory().renderNodes();
    }

}
