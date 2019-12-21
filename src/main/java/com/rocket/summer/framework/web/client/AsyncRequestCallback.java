package com.rocket.summer.framework.web.client;

import com.rocket.summer.framework.http.client.AsyncClientHttpRequest;

import java.io.IOException;

/**
 * Callback interface for code that operates on an {@link AsyncClientHttpRequest}. Allows
 * to manipulate the request headers, and write to the request body.
 *
 * <p>Used internally by the {@link AsyncRestTemplate}, but also useful for application code.
 *
 * @author Arjen Poutsma
 * @see com.rocket.summer.framework.web.client.AsyncRestTemplate#execute
 * @since 4.0
 */
public interface AsyncRequestCallback {

    /**
     * Gets called by {@link AsyncRestTemplate#execute} with an opened {@code ClientHttpRequest}.
     * Does not need to care about closing the request or about handling errors:
     * this will all be handled by the {@code RestTemplate}.
     * @param request the active HTTP request
     * @throws java.io.IOException in case of I/O errors
     */
    void doWithRequest(AsyncClientHttpRequest request) throws IOException;

}
