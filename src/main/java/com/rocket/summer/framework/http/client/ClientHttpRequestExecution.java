package com.rocket.summer.framework.http.client;

import com.rocket.summer.framework.http.HttpRequest;

import java.io.IOException;

/**
 * Represents the context of a client-side HTTP request execution.
 *
 * <p>Used to invoke the next interceptor in the interceptor chain,
 * or - if the calling interceptor is last - execute the request itself.
 *
 * @author Arjen Poutsma
 * @since 3.1
 * @see ClientHttpRequestInterceptor
 */
public interface ClientHttpRequestExecution {

    /**
     * Execute the request with the given request attributes and body,
     * and return the response.
     * @param request the request, containing method, URI, and headers
     * @param body the body of the request to execute
     * @return the response
     * @throws IOException in case of I/O errors
     */
    ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException;

}
