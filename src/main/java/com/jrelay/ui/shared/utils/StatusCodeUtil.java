package com.jrelay.ui.shared.utils;

public class StatusCodeUtil {

    public static String statusInf(int status) {
        String msg = switch (status) {
            case 100 ->
                """
                This interim response indicates 
                that the client should continue 
                the request or ignore the response 
                if the request is already finished.
                """;
            case 101 ->
                """
                This code is sent in response to an 
                Upgrade request header from the 
                client and indicates the protocol 
                the server is switching to.
                """;
            case 102 ->
                """
                Deprecated: This code was used in 
                WebDAV contexts to indicate that 
                a request has been received by the 
                server, but no status was available 
                at the time of the response.
                """;
            case 103 ->
                """
                This status code is primarily intended 
                to be used with the Link header, 
                letting the user agent start preloading 
                resources while the server prepares 
                a response or preconnect to an origin 
                from which the page will need resources.
                """;
            case 200 ->
                """
                The request succeeded. The result and 
                meaning of "success" depends on the 
                HTTP method:
                GET, HEAD, PUT or POST, TRACE
                """;
            case 201 ->
                """
                The request succeeded, and a new 
                resource was created as a result. 
                This is typically the response 
                sent after POST requests, or 
                some PUT requests.
                """;
            case 202 ->
                """
                """;
            case 203 ->
                """
                """;
            case 204 ->
                """
                """;
            case 205 ->
                """
                Tells the user agent to reset the 
                document which sent this request.
                """;
            case 206 ->
                """
                This response code is used in 
                response to a range request when 
                the client has requested a part or 
                parts of a resource.
                """;
            case 207 ->
                """
                Conveys information about multiple 
                resources, for situations where 
                multiple status codes might be 
                appropriate.
                """;
            case 208 ->
                """
                """;
            case 226 ->
                """
                """;
            case 300 ->
                """
                """;
            case 301 ->
                """
                The URL of the requested resource 
                has been changed permanently. 
                The new URL is given in the response.
                """;
            case 302 ->
                """
                """;
            case 303 ->
                """
                The server sent this response to 
                direct the client to get the requested 
                resource at another URI with a GET 
                request.
                """;
            case 304 ->
                """
                """;
            case 400 ->
                """
                The server cannot or will not process 
                the request due to something that is 
                perceived to be a client error 
                (e.g., malformed request syntax, invalid 
                request message framing, or deceptive 
                request routing).
                """;
            case 401 ->
                """
                Although the HTTP standard specifies 
                "unauthorized", semantically this 
                response means "unauthenticated". 
                That is, the client must authenticate 
                itself to get the requested response.
                """;
            case 402 ->
                """
                The initial purpose of this code was 
                for digital payment systems, however 
                this status code is rarely used and 
                no standard convention exists.
                """;
            case 403 ->
                """
                The client does not have access 
                rights to the content; that is, 
                it is unauthorized, so the server 
                is refusing to give the requested 
                resource. 
                Unlike 401 Unauthorized, 
                the client's identity is known 
                to the server.
                """;
            case 404 ->
                """
                The server cannot find the requested 
                resource. In the browser, this means 
                the URL is not recognized. In an API, 
                this can also mean that the endpoint 
                is valid but the resource itself 
                does not exist
                """;
            default -> "Invalid code";
        };
        return msg;
    }

    public static String statusSuffix(int status){
        var suffix = switch (status) {
            case 100 -> "Continue";
            case 101 -> "Switching Protocols";
            case 102 -> "Processing";
            case 103 -> "Early Hints";
            case 200 -> "OK";
            case 201 -> "Created";
            case 202 -> "Aceepted";
            case 203 -> "Non-Authoritative";
            case 204 -> "No Content";
            default -> "";
        };
        return suffix;
    }
}
