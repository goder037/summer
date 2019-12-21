package com.rocket.summer.framework.web.servlet.mvc;

import com.rocket.summer.framework.web.HttpRequestHandler;
import com.rocket.summer.framework.web.servlet.HandlerAdapter;
import com.rocket.summer.framework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Adapter to use the plain {@link com.rocket.summer.framework.web.HttpRequestHandler}
 * interface with the generic {@link com.rocket.summer.framework.web.servlet.DispatcherServlet}.
 * Supports handlers that implement the {@link LastModified} interface.
 *
 * <p>This is an SPI class, not used directly by application code.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see com.rocket.summer.framework.web.servlet.DispatcherServlet
 * @see com.rocket.summer.framework.web.HttpRequestHandler
 * @see LastModified
 * @see SimpleControllerHandlerAdapter
 */
public class HttpRequestHandlerAdapter implements HandlerAdapter {

    public boolean supports(Object handler) {
        return (handler instanceof HttpRequestHandler);
    }

    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        ((HttpRequestHandler) handler).handleRequest(request, response);
        return null;
    }

    public long getLastModified(HttpServletRequest request, Object handler) {
        if (handler instanceof LastModified) {
            return ((LastModified) handler).getLastModified(request);
        }
        return -1L;
    }

}

