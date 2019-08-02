package com.rocket.summer.framework.http.client;

import com.rocket.summer.framework.http.HttpOutputMessage;
import com.rocket.summer.framework.http.HttpRequest;
import com.rocket.summer.framework.util.concurrent.ListenableFuture;
import com.rocket.summer.framework.web.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Represents a client-side asynchronous HTTP request. Created via an
 * implementation of the {@link AsyncClientHttpRequestFactory}.
 *
 * <p>A {@code AsyncHttpRequest} can be {@linkplain #executeAsync() executed},
 * getting a future {@link ClientHttpResponse} which can be read from.
 *
 * @author Arjen Poutsma
 * @since 4.0
 * @see AsyncClientHttpRequestFactory#createAsyncRequest
 */
public interface AsyncClientHttpRequest extends HttpRequest, HttpOutputMessage {

    /**
     * Execute this request asynchronously, resulting in a Future handle.
     * {@link ClientHttpResponse} that can be read.
     * @return the future response result of the execution
     * @throws java.io.IOException in case of I/O errors
     */
    ListenableFuture<ClientHttpResponse> executeAsync() throws IOException;

}

