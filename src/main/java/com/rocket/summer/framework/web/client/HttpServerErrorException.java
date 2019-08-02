package com.rocket.summer.framework.web.client;

import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.http.HttpStatus;

import java.nio.charset.Charset;

/**
 * Exception thrown when an HTTP 5xx is received.
 *
 * @author Arjen Poutsma
 * @since 3.0
 * @see DefaultResponseErrorHandler
 */
public class HttpServerErrorException extends HttpStatusCodeException {

    private static final long serialVersionUID = -2915754006618138282L;


    /**
     * Construct a new instance of {@code HttpServerErrorException} based on
     * an {@link HttpStatus}.
     * @param statusCode the status code
     */
    public HttpServerErrorException(HttpStatus statusCode) {
        super(statusCode);
    }

    /**
     * Construct a new instance of {@code HttpServerErrorException} based on
     * an {@link HttpStatus} and status text.
     * @param statusCode the status code
     * @param statusText the status text
     */
    public HttpServerErrorException(HttpStatus statusCode, String statusText) {
        super(statusCode, statusText);
    }

    /**
     * Construct a new instance of {@code HttpServerErrorException} based on
     * an {@link HttpStatus}, status text, and response body content.
     * @param statusCode the status code
     * @param statusText the status text
     * @param responseBody the response body content (may be {@code null})
     * @param responseCharset the response body charset (may be {@code null})
     * @since 3.0.5
     */
    public HttpServerErrorException(HttpStatus statusCode, String statusText,
                                    byte[] responseBody, Charset responseCharset) {

        super(statusCode, statusText, responseBody, responseCharset);
    }

    /**
     * Construct a new instance of {@code HttpServerErrorException} based on
     * an {@link HttpStatus}, status text, and response body content.
     * @param statusCode the status code
     * @param statusText the status text
     * @param responseHeaders the response headers (may be {@code null})
     * @param responseBody the response body content (may be {@code null})
     * @param responseCharset the response body charset (may be {@code null})
     * @since 3.1.2
     */
    public HttpServerErrorException(HttpStatus statusCode, String statusText,
                                    HttpHeaders responseHeaders, byte[] responseBody, Charset responseCharset) {

        super(statusCode, statusText, responseHeaders, responseBody, responseCharset);
    }

}

