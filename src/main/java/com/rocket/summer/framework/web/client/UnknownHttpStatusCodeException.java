package com.rocket.summer.framework.web.client;

import com.rocket.summer.framework.http.HttpHeaders;
import com.rocket.summer.framework.http.HttpStatus;

import java.nio.charset.Charset;

/**
 * Exception thrown when an unknown (or custom) HTTP status code is received.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class UnknownHttpStatusCodeException extends RestClientResponseException {

    private static final long serialVersionUID = 7103980251635005491L;


    /**
     * Construct a new instance of {@code HttpStatusCodeException} based on an
     * {@link HttpStatus}, status text, and response body content.
     * @param rawStatusCode the raw status code value
     * @param statusText the status text
     * @param responseHeaders the response headers (may be {@code null})
     * @param responseBody the response body content (may be {@code null})
     * @param responseCharset the response body charset (may be {@code null})
     */
    public UnknownHttpStatusCodeException(int rawStatusCode, String statusText,
                                          HttpHeaders responseHeaders, byte[] responseBody, Charset responseCharset) {

        super("Unknown status code [" + rawStatusCode + "]" + " " + statusText,
                rawStatusCode, statusText, responseHeaders, responseBody, responseCharset);
    }

}

