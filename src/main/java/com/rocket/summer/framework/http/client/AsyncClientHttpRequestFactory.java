package com.rocket.summer.framework.http.client;

import com.rocket.summer.framework.http.HttpMethod;

import java.io.IOException;
import java.net.URI;

/**
 * Factory for {@link AsyncClientHttpRequest} objects.
 * Requests are created by the {@link #createAsyncRequest(URI, HttpMethod)} method.
 *
 * @author Arjen Poutsma
 * @since 4.0
 */
public interface AsyncClientHttpRequestFactory {

    /**
     * Create a new asynchronous {@link AsyncClientHttpRequest} for the specified URI
     * and HTTP method.
     * <p>The returned request can be written to, and then executed by calling
     * {@link AsyncClientHttpRequest#executeAsync()}.
     * @param uri the URI to create a request for
     * @param httpMethod the HTTP method to execute
     * @return the created request
     * @throws IOException in case of I/O errors
     */
    AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) throws IOException;

}
