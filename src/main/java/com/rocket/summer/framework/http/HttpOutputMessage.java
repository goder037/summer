package com.rocket.summer.framework.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents an HTTP output message, consisting of {@linkplain #getHeaders() headers}
 * and a writable {@linkplain #getBody() body}.
 *
 * <p>Typically implemented by an HTTP request on the client-side, or a response on the server-side.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public interface HttpOutputMessage extends HttpMessage {

    /**
     * Return the body of the message as an output stream.
     * @return the output stream body
     * @throws IOException in case of I/O Errors
     */
    OutputStream getBody() throws IOException;

}