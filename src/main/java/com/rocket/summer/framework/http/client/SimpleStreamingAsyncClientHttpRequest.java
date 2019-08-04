package com.rocket.summer.framework.http.client;

import com.rocket.summer.framework.core.task.AsyncListenableTaskExecutor;
import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.http.HttpMethod;
import com.rocket.summer.framework.util.StreamUtils;
import com.rocket.summer.framework.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;

/**
 * {@link com.rocket.summer.framework.http.client.ClientHttpRequest} implementation that uses
 * standard Java facilities to execute streaming requests. Created via the {@link
 * com.rocket.summer.framework.http.client.SimpleClientHttpRequestFactory}.
 *
 * @author Arjen Poutsma
 * @since 3.0
 * @see com.rocket.summer.framework.http.client.SimpleClientHttpRequestFactory#createRequest
 */
final class SimpleStreamingAsyncClientHttpRequest extends AbstractAsyncClientHttpRequest {

    private final HttpURLConnection connection;

    private final int chunkSize;

    private OutputStream body;

    private final boolean outputStreaming;

    private final AsyncListenableTaskExecutor taskExecutor;


    SimpleStreamingAsyncClientHttpRequest(HttpURLConnection connection, int chunkSize,
                                          boolean outputStreaming, AsyncListenableTaskExecutor taskExecutor) {

        this.connection = connection;
        this.chunkSize = chunkSize;
        this.outputStreaming = outputStreaming;
        this.taskExecutor = taskExecutor;
    }


    @Override
    public HttpMethod getMethod() {
        return HttpMethod.resolve(this.connection.getRequestMethod());
    }

    @Override
    public URI getURI() {
        try {
            return this.connection.getURL().toURI();
        }
        catch (URISyntaxException ex) {
            throw new IllegalStateException(
                    "Could not get HttpURLConnection URI: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
        if (this.body == null) {
            if (this.outputStreaming) {
                int contentLength = (int) headers.getContentLength();
                if (contentLength >= 0) {
                    this.connection.setFixedLengthStreamingMode(contentLength);
                }
                else {
                    this.connection.setChunkedStreamingMode(this.chunkSize);
                }
            }
            SimpleBufferingClientHttpRequest.addHeaders(this.connection, headers);
            this.connection.connect();
            this.body = this.connection.getOutputStream();
        }
        return StreamUtils.nonClosing(this.body);
    }

    @Override
    protected ListenableFuture<ClientHttpResponse> executeInternal(final HttpHeaders headers) throws IOException {
        return this.taskExecutor.submitListenable(new Callable<ClientHttpResponse>() {
            @Override
            public ClientHttpResponse call() throws Exception {
                try {
                    if (body != null) {
                        body.close();
                    }
                    else {
                        SimpleBufferingClientHttpRequest.addHeaders(connection, headers);
                        connection.connect();
                        // Immediately trigger the request in a no-output scenario as well
                        connection.getResponseCode();
                    }
                }
                catch (IOException ex) {
                    // ignore
                }
                return new SimpleClientHttpResponse(connection);
            }
        });

    }

}
