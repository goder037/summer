package com.rocket.summer.framework.web.client.support;

import com.rocket.summer.framework.http.HttpMethod;
import com.rocket.summer.framework.http.client.AsyncClientHttpRequest;
import com.rocket.summer.framework.http.client.AsyncClientHttpRequestFactory;
import com.rocket.summer.framework.util.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.URI;

/**
 * Base class for {@link com.rocket.summer.framework.web.client.AsyncRestTemplate}
 * and other HTTP accessing gateway helpers, defining common properties
 * such as the {@link AsyncClientHttpRequestFactory} to operate on.
 *
 * <p>Not intended to be used directly. See
 * {@link com.rocket.summer.framework.web.client.AsyncRestTemplate}.
 *
 * @author Arjen Poutsma
 * @since 4.0
 * @see com.rocket.summer.framework.web.client.AsyncRestTemplate
 */
public class AsyncHttpAccessor {

    /** Logger available to subclasses. */
    protected final Log logger = LogFactory.getLog(getClass());

    private AsyncClientHttpRequestFactory asyncRequestFactory;


    /**
     * Set the request factory that this accessor uses for obtaining {@link
     * com.rocket.summer.framework.http.client.ClientHttpRequest HttpRequests}.
     */
    public void setAsyncRequestFactory(AsyncClientHttpRequestFactory asyncRequestFactory) {
        Assert.notNull(asyncRequestFactory, "AsyncClientHttpRequestFactory must not be null");
        this.asyncRequestFactory = asyncRequestFactory;
    }

    /**
     * Return the request factory that this accessor uses for obtaining {@link
     * com.rocket.summer.framework.http.client.ClientHttpRequest HttpRequests}.
     */
    public AsyncClientHttpRequestFactory getAsyncRequestFactory() {
        return this.asyncRequestFactory;
    }

    /**
     * Create a new {@link AsyncClientHttpRequest} via this template's
     * {@link AsyncClientHttpRequestFactory}.
     * @param url the URL to connect to
     * @param method the HTTP method to execute (GET, POST, etc.)
     * @return the created request
     * @throws IOException in case of I/O errors
     */
    protected AsyncClientHttpRequest createAsyncRequest(URI url, HttpMethod method) throws IOException {
        AsyncClientHttpRequest request = getAsyncRequestFactory().createAsyncRequest(url, method);
        if (logger.isDebugEnabled()) {
            logger.debug("Created asynchronous " + method.name() + " request for \"" + url + "\"");
        }
        return request;
    }

}

