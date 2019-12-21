package com.rocket.summer.framework.web.client.support;

import com.rocket.summer.framework.http.client.AsyncClientHttpRequestFactory;
import com.rocket.summer.framework.http.client.AsyncClientHttpRequestInterceptor;
import com.rocket.summer.framework.http.client.InterceptingAsyncClientHttpRequestFactory;
import com.rocket.summer.framework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The HTTP accessor that extends the base {@link AsyncHttpAccessor} with
 * request intercepting functionality.
 *
 * @author Jakub Narloch
 * @author Rossen Stoyanchev
 * @since 4.3
 */
public abstract class InterceptingAsyncHttpAccessor extends AsyncHttpAccessor {

    private List<AsyncClientHttpRequestInterceptor> interceptors =
            new ArrayList<AsyncClientHttpRequestInterceptor>();


    /**
     * Set the request interceptors that this accessor should use.
     * @param interceptors the list of interceptors
     */
    public void setInterceptors(List<AsyncClientHttpRequestInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    /**
     * Return the request interceptor that this accessor uses.
     */
    public List<AsyncClientHttpRequestInterceptor> getInterceptors() {
        return this.interceptors;
    }


    @Override
    public AsyncClientHttpRequestFactory getAsyncRequestFactory() {
        AsyncClientHttpRequestFactory delegate = super.getAsyncRequestFactory();
        if (!CollectionUtils.isEmpty(getInterceptors())) {
            return new InterceptingAsyncClientHttpRequestFactory(delegate, getInterceptors());
        }
        else {
            return delegate;
        }
    }

}
