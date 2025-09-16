package com.jrelay.core.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.jrelay.core.os.OsManager;
import lombok.Getter;

public class JsonRepository<T> {
    private final File storageFile;
    private final ObjectMapper objectMapper;
    private final TypeReference<T> typeRef;
    @Getter
    private T entity;

    public JsonRepository(String name, TypeReference<T> typeRef) {
        this.storageFile = Paths.get(OsManager.getInstance().appDataDir(), name).toFile();
        this.objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.typeRef = typeRef;
    }


    /**
     * Loads the entity from the storage file if it exists; otherwise,
     * it assigns and saves the provided default value.
     * <p>
     * This method ensures that an entity is always available:
     * <ul>
     * <li>If the storage file does not exist, the given {@code defaultValue}
     * is stored and returned.</li>
     * <li>If the file exists, the entity is deserialized and returned.</li>
     * </ul>
     * </p>
     *
     * @param defaultValue the value to use and persist when the storage file is not
     *                     found
     * @return the loaded entity, or the provided {@code defaultValue} if no file
     * exists
     * @throws RepositoryException if an error occurs while reading the file
     * @author ASDFG14N
     * @since 22-08-2025
     */
    public T loadOrDefault(T defaultValue) {
        if (!storageFile.exists()) {
            this.entity = defaultValue;
            save();
        } else {
            try {
                this.entity = objectMapper.readValue(storageFile, typeRef);
            } catch (IOException e) {
                throw new RepositoryException("Error loading entity from " + storageFile, e);
            }
        }
        return entity;
    }

    /**
     * Persists the current entity into the storage file in JSON format
     * using a pretty-printed structure for readability.
     * <p>
     * If an error occurs while writing to the file, a
     * {@link RepositoryException} is thrown.
     * </p>
     *
     * @throws RepositoryException if an I/O error occurs during saving
     * @author ASDFG14N
     * @since 22-08-2025
     */
    public void save() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(storageFile, entity);
        } catch (IOException e) {
            throw new RepositoryException("Error saving entity to " + storageFile, e);
        }
    }

    public void setEntity(T entity) {
        this.entity = entity;
        save();
    }

    /**
     * Custom runtime exception used to indicate errors related to
     * repository operations, such as loading or saving entities.
     * <p>
     * This exception wraps the original cause to provide more context
     * about the failure.
     * </p>
     *
     * @author ASDFG14N
     * @since 22-08-2025
     */
    public static class RepositoryException extends RuntimeException {
        public RepositoryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}