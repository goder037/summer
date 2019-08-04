package com.rocket.summer.framework.http.client;

import com.rocket.summer.framework.core.task.AsyncListenableTaskExecutor;
import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.http.HttpMethod;
import com.rocket.summer.framework.util.FileCopyUtils;
import com.rocket.summer.framework.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;

/**
 * {@link com.rocket.summer.framework.http.client.ClientHttpRequest} implementation that uses
 * standard JDK facilities to execute buffered requests. Created via the
 * {@link com.rocket.summer.framework.http.client.SimpleClientHttpRequestFactory}.
 *
 * @author Arjen Poutsma
 * @since 3.0
 * @see com.rocket.summer.framework.http.client.SimpleClientHttpRequestFactory#createRequest
 */
final class SimpleBufferingAsyncClientHttpRequest extends AbstractBufferingAsyncClientHttpRequest {

    private final HttpURLConnection connection;

    private final boolean outputStreaming;

    private final AsyncListenableTaskExecutor taskExecutor;


    SimpleBufferingAsyncClientHttpRequest(HttpURLConnection connection,
                                          boolean outputStreaming, AsyncListenableTaskExecutor taskExecutor) {

        this.connection = connection;
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
            throw new IllegalStateException("Could not get HttpURLConnection URI: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected ListenableFuture<ClientHttpResponse> executeInternal(
            final HttpHeaders headers, final byte[] bufferedOutput) throws IOException {

        return this.taskExecutor.submitListenable(new Callable<ClientHttpResponse>() {
            @Override
            public ClientHttpResponse call() throws Exception {
                SimpleBufferingClientHttpRequest.addHeaders(connection, headers);
                // JDK <1.8 doesn't support getOutputStream with HTTP DELETE
                if (getMethod() == HttpMethod.DELETE && bufferedOutput.length == 0) {
                    connection.setDoOutput(false);
                }
                if (connection.getDoOutput() && outputStreaming) {
                    connection.setFixedLengthStreamingMode(bufferedOutput.length);
                }
                connection.connect();
                if (connection.getDoOutput()) {
                    FileCopyUtils.copy(bufferedOutput, connection.getOutputStream());
                }
                else {
                    // Immediately trigger the request in a no-output scenario as well
                    connection.getResponseCode();
                }
                return new SimpleClientHttpResponse(connection);
            }
        });
    }

}

