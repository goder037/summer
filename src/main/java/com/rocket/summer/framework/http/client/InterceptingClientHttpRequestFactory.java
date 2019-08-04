package com.rocket.summer.framework.http.client;

import com.rocket.summer.framework.http.HttpMethod;

import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * {@link ClientHttpRequestFactory} wrapper with support for {@link ClientHttpRequestInterceptor}s.
 *
 * @author Arjen Poutsma
 * @since 3.1
 * @see ClientHttpRequestFactory
 * @see ClientHttpRequestInterceptor
 */
public class InterceptingClientHttpRequestFactory extends AbstractClientHttpRequestFactoryWrapper {

    private final List<ClientHttpRequestInterceptor> interceptors;


    /**
     * Create a new instance of the {@code InterceptingClientHttpRequestFactory} with the given parameters.
     * @param requestFactory the request factory to wrap
     * @param interceptors the interceptors that are to be applied (can be {@code null})
     */
    public InterceptingClientHttpRequestFactory(ClientHttpRequestFactory requestFactory,
                                                List<ClientHttpRequestInterceptor> interceptors) {

        super(requestFactory);
        this.interceptors = (interceptors != null ? interceptors : Collections.<ClientHttpRequestInterceptor>emptyList());
    }


    @Override
    protected ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod, ClientHttpRequestFactory requestFactory) {
        return new InterceptingClientHttpRequest(requestFactory, this.interceptors, uri, httpMethod);
    }

}
