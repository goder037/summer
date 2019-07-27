package com.rocket.summer.framework.http;

/**
 * Java 5 enumeration of HTTP request methods. Intended for use
 * with {@link com.rocket.summer.framework.http.client.ClientHttpRequest}
 * and {@link com.rocket.summer.framework.web.client.RestTemplate}.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public enum HttpMethod {

    GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE

}
