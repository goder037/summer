package com.rocket.summer.framework.web;

import javax.servlet.ServletException;

/**
 * Exception thrown when an HTTP request handler requires a pre-existing session.
 *
 * @author Juergen Hoeller
 * @since 2.0
 */
public class HttpSessionRequiredException extends ServletException {

    /**
     * Create a new HttpSessionRequiredException.
     * @param msg the detail message
     */
    public HttpSessionRequiredException(String msg) {
        super(msg);
    }

}
