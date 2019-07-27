package com.rocket.summer.framework.web.servlet.mvc;

import javax.servlet.http.HttpServletRequest;

/**
 * Supports last-modified HTTP requests to facilitate content caching.
 * Same contract as for the Servlet API's <code>getLastModified</code> method.
 *
 * <p>Delegated to by a {@link com.rocket.summer.framework.web.servlet.HandlerAdapter#getLastModified}
 * implementation. By default, any Controller or HttpRequestHandler within Spring's
 * default framework can implement this interface to enable last-modified checking.
 *
 * <p><b>Note:</b> Alternative handler implementation approaches have different
 * last-modified handling styles. For example, Spring 2.5's annotated controller
 * approach (using <code>@RequestMapping</code>) provides last-modified support
 * through the {@link com.rocket.summer.framework.web.context.request.WebRequest#checkNotModified}
 * method, allowing for last-modified checking within the main handler method.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see javax.servlet.http.HttpServlet#getLastModified
 * @see Controller
 * @see SimpleControllerHandlerAdapter
 * @see com.rocket.summer.framework.web.HttpRequestHandler
 * @see HttpRequestHandlerAdapter
 */
public interface LastModified {

    /**
     * Same contract as for HttpServlet's <code>getLastModified</code> method.
     * Invoked <b>before</b> request processing.
     * <p>The return value will be sent to the HTTP client as Last-Modified header,
     * and compared with If-Modified-Since headers that the client sends back.
     * The content will only get regenerated if there has been a modification.
     * @param request current HTTP request
     * @return the time the underlying resource was last modified, or -1
     * meaning that the content must always be regenerated
     * @see com.rocket.summer.framework.web.servlet.HandlerAdapter#getLastModified
     * @see javax.servlet.http.HttpServlet#getLastModified
     */
    long getLastModified(HttpServletRequest request);

}
