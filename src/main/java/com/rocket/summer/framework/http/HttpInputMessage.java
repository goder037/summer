package com.rocket.summer.framework.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents an HTTP input message, consisting of {@linkplain #getHeaders() headers}
 * and a readable {@linkplain #getBody() body}.
 *
 * <p>Typically implemented by an HTTP request on the server-side, or a response on the client-side.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public interface HttpInputMessage extends HttpMessage {

    /**
     * Return the body of the message as an input stream.
     * @return the input stream body
     * @throws IOException in case of I/O Errors
     */
    InputStream getBody() throws IOException;

}
