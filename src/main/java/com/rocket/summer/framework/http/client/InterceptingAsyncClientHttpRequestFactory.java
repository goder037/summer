package com.rocket.summer.framework.http.client;

import com.rocket.summer.framework.http.HttpMethod;

import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * Wrapper for a {@link AsyncClientHttpRequestFactory} that has support for
 * {@link AsyncClientHttpRequestInterceptor}s.
 *
 * @author Jakub Narloch
 * @since 4.3
 * @see InterceptingAsyncClientHttpRequest
 */
public class InterceptingAsyncClientHttpRequestFactory implements AsyncClientHttpRequestFactory {

    private AsyncClientHttpRequestFactory delegate;

    private List<AsyncClientHttpRequestInterceptor> interceptors;


    /**
     * Create new instance of {@link InterceptingAsyncClientHttpRequestFactory}
     * with delegated request factory and list of interceptors.
     * @param delegate the request factory to delegate to
     * @param interceptors the list of interceptors to use
     */
    public InterceptingAsyncClientHttpRequestFactory(AsyncClientHttpRequestFactory delegate,
                                                     List<AsyncClientHttpRequestInterceptor> interceptors) {

        this.delegate = delegate;
        this.interceptors = (interceptors != null ? interceptors : Collections.<AsyncClientHttpRequestInterceptor>emptyList());
    }


    @Override
    public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod method) {
        return new InterceptingAsyncClientHttpRequest(this.delegate, this.interceptors, uri, method);
    }

}
