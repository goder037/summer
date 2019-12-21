package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.util.PathMatcher;
import com.rocket.summer.framework.web.util.UrlPathHelper;

/**
 * Helps with configuring HandlerMappings path matching options such as trailing
 * slash match, suffix registration, path matcher and path helper.
 *
 * <p>Configured path matcher and path helper instances are shared for:
 * <ul>
 * <li>RequestMappings</li>
 * <li>ViewControllerMappings</li>
 * <li>ResourcesMappings</li>
 * </ul>
 *
 * @author Brian Clozel
 * @since 4.0.3
 * @see com.rocket.summer.framework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
 * @see com.rocket.summer.framework.web.servlet.handler.SimpleUrlHandlerMapping
 */
public class PathMatchConfigurer {

    private Boolean suffixPatternMatch;

    private Boolean trailingSlashMatch;

    private Boolean registeredSuffixPatternMatch;

    private UrlPathHelper urlPathHelper;

    private PathMatcher pathMatcher;


    /**
     * Whether to use suffix pattern match (".*") when matching patterns to
     * requests. If enabled a method mapped to "/users" also matches to "/users.*".
     * <p>By default this is set to {@code true}.
     * @see #registeredSuffixPatternMatch
     */
    public PathMatchConfigurer setUseSuffixPatternMatch(Boolean suffixPatternMatch) {
        this.suffixPatternMatch = suffixPatternMatch;
        return this;
    }

    /**
     * Whether to match to URLs irrespective of the presence of a trailing slash.
     * If enabled a method mapped to "/users" also matches to "/users/".
     * <p>The default value is {@code true}.
     */
    public PathMatchConfigurer setUseTrailingSlashMatch(Boolean trailingSlashMatch) {
        this.trailingSlashMatch = trailingSlashMatch;
        return this;
    }

    /**
     * Whether suffix pattern matching should work only against path extensions
     * explicitly registered when you
     * {@link WebMvcConfigurer#configureContentNegotiation configure content
     * negotiation}. This is generally recommended to reduce ambiguity and to
     * avoid issues such as when a "." appears in the path for other reasons.
     * <p>By default this is set to "false".
     * @see WebMvcConfigurer#configureContentNegotiation
     */
    public PathMatchConfigurer setUseRegisteredSuffixPatternMatch(Boolean registeredSuffixPatternMatch) {
        this.registeredSuffixPatternMatch = registeredSuffixPatternMatch;
        return this;
    }

    /**
     * Set the UrlPathHelper to use for resolution of lookup paths.
     * <p>Use this to override the default UrlPathHelper with a custom subclass,
     * or to share common UrlPathHelper settings across multiple HandlerMappings
     * and MethodNameResolvers.
     */
    public PathMatchConfigurer setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
        return this;
    }

    /**
     * Set the PathMatcher implementation to use for matching URL paths
     * against registered URL patterns. Default is AntPathMatcher.
     * @see com.rocket.summer.framework.util.AntPathMatcher
     */
    public PathMatchConfigurer setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
        return this;
    }


    public Boolean isUseSuffixPatternMatch() {
        return this.suffixPatternMatch;
    }

    public Boolean isUseTrailingSlashMatch() {
        return this.trailingSlashMatch;
    }

    public Boolean isUseRegisteredSuffixPatternMatch() {
        return this.registeredSuffixPatternMatch;
    }

    public UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }

}

