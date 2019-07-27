package com.rocket.summer.framework.web.bind.annotation;

/**
 * Java 5 enumeration of HTTP request methods. Intended for use
 * with the {@link RequestMapping#method()} attribute of the
 * {@link RequestMapping} annotation.
 *
 * <p>Note that, by default, {@link com.rocket.summer.framework.web.servlet.DispatcherServlet}
 * supports GET, HEAD, POST, PUT and DELETE only. DispatcherServlet will
 * process TRACE and OPTIONS with the default HttpServlet behavior unless
 * explicitly told to dispatch those request types as well: Check out
 * the "dispatchOptionsRequest" and "dispatchTraceRequest" properties,
 * switching them to "true" if necessary.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see RequestMapping
 * @see com.rocket.summer.framework.web.servlet.DispatcherServlet#setDispatchOptionsRequest
 * @see com.rocket.summer.framework.web.servlet.DispatcherServlet#setDispatchTraceRequest
 */
public enum RequestMethod {

    GET, HEAD, POST, PUT, DELETE, OPTIONS, TRACE

}
