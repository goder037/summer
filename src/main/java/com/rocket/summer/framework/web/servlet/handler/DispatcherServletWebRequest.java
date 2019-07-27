package com.rocket.summer.framework.web.servlet.handler;

import com.rocket.summer.framework.web.context.request.ServletWebRequest;
import com.rocket.summer.framework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * {@link ServletWebRequest} subclass that is aware of
 * {@link com.rocket.summer.framework.web.servlet.DispatcherServlet}'s
 * request context, such as the Locale determined by the configured
 * {@link com.rocket.summer.framework.web.servlet.LocaleResolver}.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see #getLocale()
 * @see com.rocket.summer.framework.web.servlet.LocaleResolver
 */
public class DispatcherServletWebRequest extends ServletWebRequest {

    /**
     * Create a new DispatcherServletWebRequest instance for the given request.
     * @param request current HTTP request
     */
    public DispatcherServletWebRequest(HttpServletRequest request) {
        super(request);
    }

    /**
     * Create a new DispatcherServletWebRequest instance for the given request and response.
     * @param request current HTTP request
     * @param request current HTTP response
     */
    public DispatcherServletWebRequest(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    @Override
    public Locale getLocale() {
        return RequestContextUtils.getLocale(getRequest());
    }

}