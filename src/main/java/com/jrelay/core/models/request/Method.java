package com.jrelay.core.models.request;

/**
 * Represents the HTTP methods supported by the application.
 *
 * <p>
 * These methods correspond to standard HTTP operations used in RESTful APIs.
 *
 * <ul>
 * <li>GET – Retrieves data from the server without modifying it.</li>
 * <li>POST – Sends data to the server to create a new resource.</li>
 * <li>PUT – Replaces a resource on the server with the provided data.</li>
 * <li>PATCH – Applies partial modifications to a resource.</li>
 * <li>DELETE – Removes the specified resource from the server.</li>
 * <li>OPTIONS – Describes the communication options for the target
 * resource.</li>
 * </ul>
 * 
 * @author @ASDG14N
 * @since 05-08-2025
 */

public enum Method {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS
}
