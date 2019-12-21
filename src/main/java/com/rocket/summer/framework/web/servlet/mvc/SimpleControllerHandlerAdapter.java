package com.rocket.summer.framework.web.servlet.mvc;

import com.rocket.summer.framework.web.servlet.HandlerAdapter;
import com.rocket.summer.framework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Adapter to use the plain {@link Controller} workflow interface with
 * the generic {@link com.rocket.summer.framework.web.servlet.DispatcherServlet}.
 * Supports handlers that implement the {@link LastModified} interface.
 *
 * <p>This is an SPI class, not used directly by application code.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see com.rocket.summer.framework.web.servlet.DispatcherServlet
 * @see Controller
 * @see LastModified
 * @see HttpRequestHandlerAdapter
 */
public class SimpleControllerHandlerAdapter implements HandlerAdapter {

    public boolean supports(Object handler) {
        return (handler instanceof Controller);
    }

    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        return ((Controller) handler).handleRequest(request, response);
    }

    public long getLastModified(HttpServletRequest request, Object handler) {
        if (handler instanceof LastModified) {
            return ((LastModified) handler).getLastModified(request);
        }
        return -1L;
    }

}
