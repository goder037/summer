package com.rocket.summer.framework.web.client;

import java.io.IOException;

/**
 * Callback interface for code that operates on a {@link ClientHttpRequest}. Allows to manipulate the request
 * headers, and write to the request body.
 *
 * <p>Used internally by the {@link RestTemplate}, but also useful for application code.
 *
 * @author Arjen Poutsma
 * @see RestTemplate#execute
 * @since 3.0
 */
public interface RequestCallback {

    /**
     * Gets called by {@link RestTemplate#execute} with an opened {@code ClientHttpRequest}.
     * Does not need to care about closing the request or about handling errors:
     * this will all be handled by the {@code RestTemplate}.
     * @param request the active HTTP request
     * @throws IOException in case of I/O errors
     */
    void doWithRequest(ClientHttpRequest request) throws IOException;

}
