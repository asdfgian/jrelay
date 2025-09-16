package com.jrelay.core.controller;

import java.util.List;

import com.jrelay.core.models.Collection;
import com.jrelay.core.models.request.Request;
import com.jrelay.core.repository.CollectionRepository;

public class CollectionController {
    private final CollectionRepository repository;

    public CollectionController() {
        this.repository = new CollectionRepository("collections.json");
    }

    public List<Collection> findAll() {
        return repository.findAll();
    }

    public void saveCollection(Collection collection) {
        repository.saveCollection(collection);
    }

    public void saveRequest(Request request) {
        repository.saveRequest(request);
    }

    public Request findRequestById(String idCollection, String idRequest) {
        return repository.findRequestById(idCollection, idRequest);
    }

    public void deleteCollectionById(String id) {
        repository.deleteCollectionById(id);
    }

    public void deleteRequestById(String idCollection, String idRequest) {
        repository.deleteRequestById(idCollection, idRequest);
    }

    public void updateRequestById(String idCollection, String idRequest, Request request) {
        repository.updateRequestById(idCollection, idRequest, request);
    }
}
