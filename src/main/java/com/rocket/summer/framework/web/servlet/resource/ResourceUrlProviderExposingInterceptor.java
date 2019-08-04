package com.rocket.summer.framework.web.servlet.resource;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An interceptor that exposes the {@link ResourceUrlProvider} instance it
 * is configured with as a request attribute.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class ResourceUrlProviderExposingInterceptor extends HandlerInterceptorAdapter {

    /**
     * Name of the request attribute that holds the {@link ResourceUrlProvider}.
     */
    public static final String RESOURCE_URL_PROVIDER_ATTR = ResourceUrlProvider.class.getName();

    private final ResourceUrlProvider resourceUrlProvider;


    public ResourceUrlProviderExposingInterceptor(ResourceUrlProvider resourceUrlProvider) {
        Assert.notNull(resourceUrlProvider, "ResourceUrlProvider is required");
        this.resourceUrlProvider = resourceUrlProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        request.setAttribute(RESOURCE_URL_PROVIDER_ATTR, this.resourceUrlProvider);
        return true;
    }

}
