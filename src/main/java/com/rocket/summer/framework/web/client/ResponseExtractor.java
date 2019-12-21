package com.rocket.summer.framework.web.client;

import com.rocket.summer.framework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Generic callback interface used by {@link RestTemplate}'s retrieval methods
 * Implementations of this interface perform the actual work of extracting data
 * from a {@link ClientHttpResponse}, but don't need to worry about exception
 * handling or closing resources.
 *
 * <p>Used internally by the {@link RestTemplate}, but also useful for application code.
 *
 * @author Arjen Poutsma
 * @since 3.0
 * @see RestTemplate#execute
 */
public interface ResponseExtractor<T> {

    /**
     * Extract data from the given {@code ClientHttpResponse} and return it.
     * @param response the HTTP response
     * @return the extracted data
     * @throws IOException in case of I/O errors
     */
    T extractData(ClientHttpResponse response) throws IOException;

}