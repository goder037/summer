package com.rocket.summer.framework.web;

import javax.servlet.ServletException;

import javax.servlet.ServletException;

/**
 * Exception thrown when an HTTP request handler requires a pre-existing session.
 *
 * @author Juergen Hoeller
 * @since 2.0
 */
@SuppressWarnings("serial")
public class HttpSessionRequiredException extends ServletException {

    private String expectedAttribute;


    /**
     * Create a new HttpSessionRequiredException.
     * @param msg the detail message
     */
    public HttpSessionRequiredException(String msg) {
        super(msg);
    }

    /**
     * Create a new HttpSessionRequiredException.
     * @param msg the detail message
     * @param expectedAttribute the name of the expected session attribute
     * @since 4.3
     */
    public HttpSessionRequiredException(String msg, String expectedAttribute) {
        super(msg);
        this.expectedAttribute = expectedAttribute;
    }


    /**
     * Return the name of the expected session attribute, if any.
     * @since 4.3
     */
    public String getExpectedAttribute() {
        return this.expectedAttribute;
    }

}
