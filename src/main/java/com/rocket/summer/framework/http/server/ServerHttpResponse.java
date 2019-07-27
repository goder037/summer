package com.rocket.summer.framework.http.server;

import com.rocket.summer.framework.http.HttpOutputMessage;
import com.rocket.summer.framework.http.HttpStatus;

/**
 * Represents a server-side HTTP response.
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public interface ServerHttpResponse extends HttpOutputMessage {

    /**
     * Set the HTTP status code of the response.
     * @param status the HTTP status as an HttpStatus enum value
     */
    void setStatusCode(HttpStatus status);

    /**
     * Close this response, freeing any resources created.
     */
    void close();

}
