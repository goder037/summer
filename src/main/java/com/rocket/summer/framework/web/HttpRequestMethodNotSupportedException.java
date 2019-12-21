package com.rocket.summer.framework.web;

import javax.servlet.ServletException;
import java.util.Collection;

/**
 * Exception thrown when a request handler does not support a
 * specific request method.
 *
 * @author Juergen Hoeller
 * @since 2.0
 */
public class HttpRequestMethodNotSupportedException extends ServletException {

    private String method;

    private String[] supportedMethods;


    /**
     * Create a new HttpRequestMethodNotSupportedException.
     * @param method the unsupported HTTP request method
     */
    public HttpRequestMethodNotSupportedException(String method) {
        this(method, (String[]) null);
    }

    /**
     * Create a new HttpRequestMethodNotSupportedException.
     * @param method the unsupported HTTP request method
     * @param supportedMethods the actually supported HTTP methods
     */
    public HttpRequestMethodNotSupportedException(String method, String[] supportedMethods) {
        this(method, supportedMethods, "Request method '" + method + "' not supported");
    }

    /**
     * Create a new HttpRequestMethodNotSupportedException.
     * @param method the unsupported HTTP request method
     * @param supportedMethods the actually supported HTTP methods
     */
    public HttpRequestMethodNotSupportedException(String method, Collection<String> supportedMethods) {
        this(method, supportedMethods.toArray(new String[supportedMethods.size()]));
    }

    /**
     * Create a new HttpRequestMethodNotSupportedException.
     * @param method the unsupported HTTP request method
     * @param msg the detail message
     */
    public HttpRequestMethodNotSupportedException(String method, String msg) {
        this(method, null, msg);
    }

    /**
     * Create a new HttpRequestMethodNotSupportedException.
     * @param method the unsupported HTTP request method
     * @param supportedMethods the actually supported HTTP methods
     * @param msg the detail message
     */
    public HttpRequestMethodNotSupportedException(String method, String[] supportedMethods, String msg) {
        super(msg);
        this.method = method;
        this.supportedMethods = supportedMethods;
    }


    /**
     * Return the HTTP request method that caused the failure.
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * Return the actually supported HTTP methods, if known.
     */
    public String[] getSupportedMethods() {
        return this.supportedMethods;
    }

}