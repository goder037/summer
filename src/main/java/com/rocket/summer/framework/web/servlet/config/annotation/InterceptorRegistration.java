package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.web.servlet.HandlerInterceptor;
import com.rocket.summer.framework.web.servlet.handler.MappedInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Encapsulates a {@link HandlerInterceptor} and an optional list of URL patterns.
 * Results in the creation of a {@link MappedInterceptor} if URL patterns are provided.
 *
 * @author Rossen Stoyanchev
 * @author Keith Donald
 * @since 3.1
 */
public class InterceptorRegistration {

    private final HandlerInterceptor interceptor;

    private final List<String> pathPatterns = new ArrayList<String>();

    /**
     * Creates an {@link InterceptorRegistration} instance.
     */
    public InterceptorRegistration(HandlerInterceptor interceptor) {
        Assert.notNull(interceptor, "Interceptor is required");
        this.interceptor = interceptor;
    }

    /**
     * Adds one or more URL patterns to which the registered interceptor should apply to.
     * If no URL patterns are provided, the interceptor applies to all paths.
     */
    public void addPathPatterns(String... pathPatterns) {
        this.pathPatterns.addAll(Arrays.asList(pathPatterns));
    }

    /**
     * Returns the underlying interceptor. If URL patterns are provided the returned type is
     * {@link MappedInterceptor}; otherwise {@link HandlerInterceptor}.
     */
    protected Object getInterceptor() {
        if (pathPatterns.isEmpty()) {
            return interceptor;
        }
        return new MappedInterceptor(pathPatterns.toArray(new String[pathPatterns.size()]), interceptor);
    }

}

