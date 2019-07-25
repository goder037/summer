package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.web.context.request.WebRequestInterceptor;
import com.rocket.summer.framework.web.servlet.HandlerInterceptor;
import com.rocket.summer.framework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores and provides access to a list of interceptors. For each interceptor you can optionally
 * specify one or more URL patterns it applies to.
 *
 * @author Rossen Stoyanchev
 * @author Keith Donald
 *
 * @since 3.1
 */
public class InterceptorRegistry {

    private final List<InterceptorRegistration> registrations = new ArrayList<InterceptorRegistration>();

    /**
     * Adds the provided {@link HandlerInterceptor}.
     * @param interceptor the interceptor to add
     * @return An {@link InterceptorRegistration} that allows you optionally configure the
     * registered interceptor further for example adding URL patterns it should apply to.
     */
    public InterceptorRegistration addInterceptor(HandlerInterceptor interceptor) {
        InterceptorRegistration registration = new InterceptorRegistration(interceptor);
        registrations.add(registration);
        return registration;
    }

    /**
     * Adds the provided {@link WebRequestInterceptor}.
     * @param interceptor the interceptor to add
     * @return An {@link InterceptorRegistration} that allows you optionally configure the
     * registered interceptor further for example adding URL patterns it should apply to.
     */
    public InterceptorRegistration addWebRequestInterceptor(WebRequestInterceptor interceptor) {
        WebRequestHandlerInterceptorAdapter adapted = new WebRequestHandlerInterceptorAdapter(interceptor);
        InterceptorRegistration registration = new InterceptorRegistration(adapted);
        registrations.add(registration);
        return registration;
    }

    /**
     * Returns all registered interceptors.
     */
    protected List<Object> getInterceptors() {
        List<Object> interceptors = new ArrayList<Object>();
        for (InterceptorRegistration registration : registrations) {
            interceptors.add(registration.getInterceptor());
        }
        return interceptors ;
    }

}

