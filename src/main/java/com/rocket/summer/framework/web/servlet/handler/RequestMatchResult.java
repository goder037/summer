package com.rocket.summer.framework.web.servlet.handler;

import java.util.Map;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.PathMatcher;

/**
 * Container for the result from request pattern matching via
 * {@link MatchableHandlerMapping} with a method to further extract
 * URI template variables from the pattern.
 *
 * @author Rossen Stoyanchev
 * @since 4.3.1
 */
public class RequestMatchResult {

    private final String matchingPattern;

    private final String lookupPath;

    private final PathMatcher pathMatcher;


    /**
     * Create an instance with a matching pattern.
     * @param matchingPattern the matching pattern, possibly not the same as the
     * input pattern, e.g. inputPattern="/foo" and matchingPattern="/foo/".
     * @param lookupPath the lookup path extracted from the request
     * @param pathMatcher the PathMatcher used
     */
    public RequestMatchResult(String matchingPattern, String lookupPath, PathMatcher pathMatcher) {
        Assert.hasText(matchingPattern, "'matchingPattern' is required");
        Assert.hasText(lookupPath, "'lookupPath' is required");
        Assert.notNull(pathMatcher, "'pathMatcher' is required");
        this.matchingPattern = matchingPattern;
        this.lookupPath = lookupPath;
        this.pathMatcher = pathMatcher;
    }


    /**
     * Extract URI template variables from the matching pattern as defined in
     * {@link PathMatcher#extractUriTemplateVariables}.
     * @return a map with URI template variables
     */
    public Map<String, String> extractUriTemplateVariables() {
        return this.pathMatcher.extractUriTemplateVariables(this.matchingPattern, this.lookupPath);
    }

}

