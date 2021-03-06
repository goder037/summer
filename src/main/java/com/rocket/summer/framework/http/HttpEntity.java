package com.rocket.summer.framework.http;

import com.rocket.summer.framework.util.MultiValueMap;

/**
 * Represents an HTTP request or response entity, consisting of headers and body.
 *
 * <p>Typically used in combination with the {@link com.rocket.summer.framework.web.client.RestTemplate RestTemplate}, like so:
 * <pre class="code">
 * HttpHeaders headers = new HttpHeaders();
 * headers.setContentType(MediaType.TEXT_PLAIN);
 * HttpEntity&lt;String&gt; entity = new HttpEntity&lt;String&gt;(helloWorld, headers);
 * URI location = template.postForLocation("http://example.com", entity);
 * </pre>
 * or
 * <pre class="code">
 * HttpEntity&lt;String&gt; entity = template.getForEntity("http://example.com", String.class);
 * String body = entity.getBody();
 * MediaType contentType = entity.getHeaders().getContentType();
 * </pre>
 * Can also be used in Spring MVC, as a return value from a @Controller method:
 * <pre class="code">
 * &#64;RequestMapping("/handle")
 * public HttpEntity&lt;String&gt; handle() {
 *   HttpHeaders responseHeaders = new HttpHeaders();
 *   responseHeaders.set("MyResponseHeader", "MyValue");
 *   return new HttpEntity&lt;String&gt;("Hello World", responseHeaders);
 * }
 * </pre>
 *
 * @author Arjen Poutsma
 * @since 3.0.2
 * @see com.rocket.summer.framework.web.client.RestTemplate
 * @see #getBody()
 * @see #getHeaders()
 */
public class HttpEntity<T> {

    /**
     * The empty {@code HttpEntity}, with no body or headers.
     */
    public static final HttpEntity EMPTY = new HttpEntity();


    private final HttpHeaders headers;

    private final T body;


    /**
     * Create a new, empty {@code HttpEntity}.
     */
    protected HttpEntity() {
        this(null, null);
    }

    /**
     * Create a new {@code HttpEntity} with the given body and no headers.
     * @param body the entity body
     */
    public HttpEntity(T body) {
        this(body, null);
    }

    /**
     * Create a new {@code HttpEntity} with the given headers and no body.
     * @param headers the entity headers
     */
    public HttpEntity(MultiValueMap<String, String> headers) {
        this(null, headers);
    }

    /**
     * Create a new {@code HttpEntity} with the given body and headers.
     * @param body the entity body
     * @param headers the entity headers
     */
    public HttpEntity(T body, MultiValueMap<String, String> headers) {
        this.body = body;
        HttpHeaders tempHeaders = new HttpHeaders();
        if (headers != null) {
            tempHeaders.putAll(headers);
        }
        this.headers = HttpHeaders.readOnlyHttpHeaders(tempHeaders);
    }


    /**
     * Returns the headers of this entity.
     */
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    /**
     * Returns the body of this entity.
     */
    public T getBody() {
        return this.body;
    }

    /**
     * Indicates whether this entity has a body.
     */
    public boolean hasBody() {
        return (this.body != null);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("<");
        if (body != null) {
            builder.append(body);
            if (headers != null) {
                builder.append(',');
            }
        }
        if (headers != null) {
            builder.append(headers);
        }
        builder.append('>');
        return builder.toString();
    }
}
