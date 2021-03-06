package com.rocket.summer.framework.web.context.request;

import com.rocket.summer.framework.context.i18n.LocaleContextHolder;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/**
 * Servlet 2.4+ listener that exposes the request to the current thread,
 * through both {@link com.rocket.summer.framework.context.i18n.LocaleContextHolder} and
 * {@link RequestContextHolder}. To be registered as listener in <code>web.xml</code>.
 *
 * <p>Alternatively, Spring's {@link com.rocket.summer.framework.web.filter.RequestContextFilter}
 * and Spring's {@link com.rocket.summer.framework.web.servlet.DispatcherServlet} also expose
 * the same request context to the current thread. In contrast to this listener,
 * advanced options are available there (e.g. "threadContextInheritable").
 *
 * <p>This listener is mainly for use with third-party servlets, e.g. the JSF FacesServlet.
 * Within Spring's own web support, DispatcherServlet's processing is perfectly sufficient.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see javax.servlet.ServletRequestListener
 * @see com.rocket.summer.framework.context.i18n.LocaleContextHolder
 * @see com.rocket.summer.framework.web.context.request.RequestContextHolder
 * @see com.rocket.summer.framework.web.filter.RequestContextFilter
 * @see com.rocket.summer.framework.web.servlet.DispatcherServlet
 */
public class RequestContextListener implements ServletRequestListener {

    private static final String REQUEST_ATTRIBUTES_ATTRIBUTE =
            RequestContextListener.class.getName() + ".REQUEST_ATTRIBUTES";


    public void requestInitialized(ServletRequestEvent requestEvent) {
        if (!(requestEvent.getServletRequest() instanceof HttpServletRequest)) {
            throw new IllegalArgumentException(
                    "Request is not an HttpServletRequest: " + requestEvent.getServletRequest());
        }
        HttpServletRequest request = (HttpServletRequest) requestEvent.getServletRequest();
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        request.setAttribute(REQUEST_ATTRIBUTES_ATTRIBUTE, attributes);
        LocaleContextHolder.setLocale(request.getLocale());
        RequestContextHolder.setRequestAttributes(attributes);
    }

    public void requestDestroyed(ServletRequestEvent requestEvent) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) requestEvent.getServletRequest().getAttribute(REQUEST_ATTRIBUTES_ATTRIBUTE);
        ServletRequestAttributes threadAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (threadAttributes != null) {
            // We're assumably within the original request thread...
            if (attributes == null) {
                attributes = threadAttributes;
            }
            LocaleContextHolder.resetLocaleContext();
            RequestContextHolder.resetRequestAttributes();
        }
        if (attributes != null) {
            attributes.requestCompleted();
        }
    }

}

