package com.rocket.summer.framework.http.client;

import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Base implementation of {@link AsyncClientHttpRequest} that buffers output
 * in a byte array before sending it over the wire.
 *
 * @author Arjen Poutsma
 * @since 4.0
 */
abstract class AbstractBufferingAsyncClientHttpRequest extends AbstractAsyncClientHttpRequest {

    private ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream(1024);


    @Override
    protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
        return this.bufferedOutput;
    }

    @Override
    protected ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers) throws IOException {
        byte[] bytes = this.bufferedOutput.toByteArray();
        if (headers.getContentLength() < 0) {
            headers.setContentLength(bytes.length);
        }
        ListenableFuture<ClientHttpResponse> result = executeInternal(headers, bytes);
        this.bufferedOutput = null;
        return result;
    }

    /**
     * Abstract template method that writes the given headers and content to the HTTP request.
     * @param headers the HTTP headers
     * @param bufferedOutput the body content
     * @return the response object for the executed request
     */
    protected abstract ListenableFuture<ClientHttpResponse> executeInternal(
            HttpHeaders headers, byte[] bufferedOutput) throws IOException;

}

