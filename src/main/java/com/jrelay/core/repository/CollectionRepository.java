package com.jrelay.core.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jrelay.core.models.Collection;
import com.jrelay.core.models.request.Request;

public class CollectionRepository extends JsonRepository<List<Collection>> {

    private final List<Collection> collections;

    public CollectionRepository(String path) {
        super(path, new TypeReference<>() {
        });
        this.collections = loadOrDefault(new ArrayList<>());
    }

    public List<Collection> findAll() {
        return Collections.unmodifiableList(collections);
    }

    public void saveCollection(Collection collection) {
        collections.add(collection);
        setEntity(collections);
    }

    public void saveRequest(Request request) {
        final var idCollection = request.getIdCollection();
        Collection collection = findCollectionById(idCollection);
        if (collection == null) {
            throw new RepositoryException("Collection with id " + idCollection + " not found", null);
        }
        collection.getRequests().add(request);
        setEntity(collections);
    }

    public Collection findCollectionById(String id) {
        return collections.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Request findRequestById(String collectionId, String requestId) {
        Collection collection = findCollectionById(collectionId);
        if (collection == null) {
            return null;
        }
        return findRequestByIdInList(requestId, collection.getRequests());
    }

    public void deleteCollectionById(String id) {
        boolean removed = collections.removeIf(c -> c.getId().equals(id));
        if (removed) {
            setEntity(collections);
        }
    }

    public void deleteRequestById(String collectionId, String requestId) {
        Collection collection = findCollectionById(collectionId);
        if (collection == null) {
            return;
        }
        boolean removed = collection.getRequests().removeIf(r -> r.getIdRequest().equals(requestId));
        if (removed) {
            setEntity(collections);
        }
    }

    public void updateRequestById(String collectionId, String requestId, Request request) {
        Collection collection = findCollectionById(collectionId);
        if (collection == null) {
            throw new RepositoryException("Collection with id " + collectionId + " not found", null);
        }
        Request req = findRequestByIdInList(requestId, collection.getRequests());
        if (req == null) {
            throw new RepositoryException("Request with id " + requestId + " not found in collection " + collectionId,
                    null);
        }
        req.setName(request.getName());
        req.setMethod(request.getMethod());
        req.setUrl(request.getUrl());
        req.setParams(request.getParams());
        req.setHeaders(request.getHeaders());
        req.setAuth(request.getAuth());
        req.setBody(request.getBody());
        setEntity(collections);
    }

    private Request findRequestByIdInList(String id, List<Request> requests) {
        return requests.stream()
                .filter(r -> r.getIdRequest().equals(id))
                .findFirst()
                .orElse(null);
    }
}
