package com.jrelay.core.models;

import java.util.List;
import java.util.UUID;

import com.jrelay.core.models.request.Request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a collection of HTTP requests, typically grouped under a common
 * name.
 * Used to organize and manage related requests in the application.
 *
 * <p>
 * This class includes an optional unique identifier, a name for the collection,
 * and a list of {@link Request} objects that belong to this collection.
 *
 * <p>
 * The identifier may be generated and assigned by the persistence layer or the
 * UI logic.
 *
 * @author @ASDG14N
 * @since 05-08-2025
 */

@Getter
@NoArgsConstructor
public class Collection {
    private String id;
    @Setter
    private String name;
    @Setter
    private List<Request> requests;

    public Collection(String name, List<Request> requests) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.requests = requests;
    }

}
