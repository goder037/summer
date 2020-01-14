package com.rocket.summer.framework.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rocket.summer.framework.http.HttpHeaders;

/**
 * By default when the DispatcherServlet can't find a handler for a request it
 * sends a 404 response. However if its property "throwExceptionIfNoHandlerFound"
 * is set to {@code true} this exception is raised and may be handled with
 * a configured HandlerExceptionResolver.
 *
 * @author Brian Clozel
 * @since 4.0
 * @see DispatcherServlet#setThrowExceptionIfNoHandlerFound(boolean)
 * @see DispatcherServlet#noHandlerFound(HttpServletRequest, HttpServletResponse)
 */
@SuppressWarnings("serial")
public class NoHandlerFoundException extends ServletException {

    private final String httpMethod;

    private final String requestURL;

    private final HttpHeaders headers;


    /**
     * Constructor for NoHandlerFoundException.
     * @param httpMethod the HTTP method
     * @param requestURL the HTTP request URL
     * @param headers the HTTP request headers
     */
    public NoHandlerFoundException(String httpMethod, String requestURL, HttpHeaders headers) {
        super("No handler found for " + httpMethod + " " + requestURL);
        this.httpMethod = httpMethod;
        this.requestURL = requestURL;
        this.headers = headers;
    }


    public String getHttpMethod() {
        return this.httpMethod;
    }

    public String getRequestURL() {
        return this.requestURL;
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

}

