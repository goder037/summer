package com.rocket.summer.framework.web.servlet.handler;

import com.rocket.summer.framework.util.PathMatcher;
import com.rocket.summer.framework.web.context.request.WebRequestInterceptor;
import com.rocket.summer.framework.web.servlet.HandlerInterceptor;

/**
 * Holds information about a HandlerInterceptor mapped to a path into the application.
 * Provides a method to match a request path to the mapped path patterns.
 *
 * @author Keith Donald
 * @author Rossen Stoyanchev
 * @since 3.0
 */
public final class MappedInterceptor {

    private final String[] pathPatterns;

    private final HandlerInterceptor interceptor;


    /**
     * Create a new MappedInterceptor instance.
     * @param pathPatterns the path patterns to map with a {@code null} value matching to all paths
     * @param interceptor the HandlerInterceptor instance to map to the given patterns
     */
    public MappedInterceptor(String[] pathPatterns, HandlerInterceptor interceptor) {
        this.pathPatterns = pathPatterns;
        this.interceptor = interceptor;
    }

    /**
     * Create a new MappedInterceptor instance.
     * @param pathPatterns the path patterns to map with a {@code null} value matching to all paths
     * @param interceptor the WebRequestInterceptor instance to map to the given patterns
     */
    public MappedInterceptor(String[] pathPatterns, WebRequestInterceptor interceptor) {
        this.pathPatterns = pathPatterns;
        this.interceptor = new WebRequestHandlerInterceptorAdapter(interceptor);
    }


    /**
     * The path into the application the interceptor is mapped to.
     */
    public String[] getPathPatterns() {
        return this.pathPatterns;
    }

    /**
     * The actual Interceptor reference.
     */
    public HandlerInterceptor getInterceptor() {
        return this.interceptor;
    }

    /**
     * Returns {@code true} if the interceptor applies to the given request path.
     * @param lookupPath the current request path
     * @param pathMatcher a path matcher for path pattern matching
     */
    public boolean matches(String lookupPath, PathMatcher pathMatcher) {
        if (pathPatterns == null) {
            return true;
        }
        else {
            for (String pathPattern : pathPatterns) {
                if (pathMatcher.match(pathPattern, lookupPath)) {
                    return true;
                }
            }
            return false;
        }
    }
}

