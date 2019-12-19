package com.rocket.summer.framework.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;

import com.rocket.summer.framework.web.servlet.HandlerMapping;

/**
 * Additional interface that a {@link HandlerMapping} can implement to expose
 * a request matching API aligned with its internal request matching
 * configuration and implementation.
 *
 * @author Rossen Stoyanchev
 * @since 4.3.1
 * @see HandlerMappingIntrospector
 */
public interface MatchableHandlerMapping extends HandlerMapping {

    /**
     * Determine whether the given request matches the request criteria.
     * @param request the current request
     * @param pattern the pattern to match
     * @return the result from request matching, or {@code null} if none
     */
    RequestMatchResult match(HttpServletRequest request, String pattern);

}

