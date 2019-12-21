package com.rocket.summer.framework.http.client.support;

import com.rocket.summer.framework.http.HttpMethod;
import com.rocket.summer.framework.http.client.ClientHttpRequest;
import com.rocket.summer.framework.http.client.ClientHttpRequestFactory;
import com.rocket.summer.framework.http.client.SimpleClientHttpRequestFactory;
import com.rocket.summer.framework.util.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.URI;

/**
 * Base class for {@link com.rocket.summer.framework.web.client.RestTemplate}
 * and other HTTP accessing gateway helpers, defining common properties
 * such as the {@link ClientHttpRequestFactory} to operate on.
 *
 * <p>Not intended to be used directly.
 * See {@link com.rocket.summer.framework.web.client.RestTemplate}.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 * @see com.rocket.summer.framework.web.client.RestTemplate
 */
public abstract class HttpAccessor {

    /** Logger available to subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private ClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();


    /**
     * Set the request factory that this accessor uses for obtaining client request handles.
     * <p>The default is a {@link SimpleClientHttpRequestFactory} based on the JDK's own
     * HTTP libraries ({@link java.net.HttpURLConnection}).
     * <p><b>Note that the standard JDK HTTP library does not support the HTTP PATCH method.
     * Configure the Apache HttpComponents or OkHttp request factory to enable PATCH.</b>
     * @see #createRequest(URI, HttpMethod)
     */
    public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
        Assert.notNull(requestFactory, "ClientHttpRequestFactory must not be null");
        this.requestFactory = requestFactory;
    }

    /**
     * Return the request factory that this accessor uses for obtaining client request handles.
     */
    public ClientHttpRequestFactory getRequestFactory() {
        return this.requestFactory;
    }


    /**
     * Create a new {@link ClientHttpRequest} via this template's {@link ClientHttpRequestFactory}.
     * @param url the URL to connect to
     * @param method the HTTP method to execute (GET, POST, etc)
     * @return the created request
     * @throws IOException in case of I/O errors
     * @see #getRequestFactory()
     * @see ClientHttpRequestFactory#createRequest(URI, HttpMethod)
     */
    protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
        ClientHttpRequest request = getRequestFactory().createRequest(url, method);
        if (logger.isDebugEnabled()) {
            logger.debug("Created " + method.name() + " request for \"" + url + "\"");
        }
        return request;
    }


}

