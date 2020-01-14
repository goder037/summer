package com.rocket.summer.framework.web.servlet.mvc;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rocket.summer.framework.http.CacheControl;
import com.rocket.summer.framework.util.AntPathMatcher;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.PathMatcher;
import com.rocket.summer.framework.web.servlet.HandlerInterceptor;
import com.rocket.summer.framework.web.servlet.ModelAndView;
import com.rocket.summer.framework.web.servlet.support.WebContentGenerator;
import com.rocket.summer.framework.web.util.UrlPathHelper;

/**
 * Handler interceptor that checks the request and prepares the response.
 * Checks for supported methods and a required session, and applies the
 * specified {@link com.rocket.summer.framework.http.CacheControl} builder.
 * See superclass bean properties for configuration options.
 *
 * <p>All the settings supported by this interceptor can also be set on
 * {@link AbstractController}. This interceptor is mainly intended for applying
 * checks and preparations to a set of controllers mapped by a HandlerMapping.
 *
 * @author Juergen Hoeller
 * @author Brian Clozel
 * @since 27.11.2003
 * @see AbstractController
 */
public class WebContentInterceptor extends WebContentGenerator implements HandlerInterceptor {

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    private PathMatcher pathMatcher = new AntPathMatcher();

    private Map<String, Integer> cacheMappings = new HashMap<String, Integer>();

    private Map<String, CacheControl> cacheControlMappings = new HashMap<String, CacheControl>();


    public WebContentInterceptor() {
        // No restriction of HTTP methods by default,
        // in particular for use with annotated controllers...
        super(false);
    }


    /**
     * Set if URL lookup should always use full path within current servlet
     * context. Else, the path within the current servlet mapping is used
     * if applicable (i.e. in the case of a ".../*" servlet mapping in web.xml).
     * Default is "false".
     * <p>Only relevant for the "cacheMappings" setting.
     * @see #setCacheMappings
     * @see com.rocket.summer.framework.web.util.UrlPathHelper#setAlwaysUseFullPath
     */
    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
        this.urlPathHelper.setAlwaysUseFullPath(alwaysUseFullPath);
    }

    /**
     * Set if context path and request URI should be URL-decoded.
     * Both are returned <i>undecoded</i> by the Servlet API,
     * in contrast to the servlet path.
     * <p>Uses either the request encoding or the default encoding according
     * to the Servlet spec (ISO-8859-1).
     * <p>Only relevant for the "cacheMappings" setting.
     * @see #setCacheMappings
     * @see com.rocket.summer.framework.web.util.UrlPathHelper#setUrlDecode
     */
    public void setUrlDecode(boolean urlDecode) {
        this.urlPathHelper.setUrlDecode(urlDecode);
    }

    /**
     * Set the UrlPathHelper to use for resolution of lookup paths.
     * <p>Use this to override the default UrlPathHelper with a custom subclass,
     * or to share common UrlPathHelper settings across multiple HandlerMappings
     * and MethodNameResolvers.
     * <p>Only relevant for the "cacheMappings" setting.
     * @see #setCacheMappings
     * @see com.rocket.summer.framework.web.servlet.handler.AbstractUrlHandlerMapping#setUrlPathHelper
     */
    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
        this.urlPathHelper = urlPathHelper;
    }

    /**
     * Map specific URL paths to specific cache seconds.
     * <p>Overrides the default cache seconds setting of this interceptor.
     * Can specify "-1" to exclude a URL path from default caching.
     * <p>Supports direct matches, e.g. a registered "/test" matches "/test",
     * and a various Ant-style pattern matches, e.g. a registered "/t*" matches
     * both "/test" and "/team". For details, see the AntPathMatcher javadoc.
     * <p><b>NOTE:</b> Path patterns are not supposed to overlap. If a request
     * matches several mappings, it is effectively undefined which one will apply
     * (due to the lack of key ordering in {@code java.util.Properties}).
     * @param cacheMappings a mapping between URL paths (as keys) and
     * cache seconds (as values, need to be integer-parsable)
     * @see #setCacheSeconds
     * @see com.rocket.summer.framework.util.AntPathMatcher
     */
    public void setCacheMappings(Properties cacheMappings) {
        this.cacheMappings.clear();
        Enumeration<?> propNames = cacheMappings.propertyNames();
        while (propNames.hasMoreElements()) {
            String path = (String) propNames.nextElement();
            int cacheSeconds = Integer.valueOf(cacheMappings.getProperty(path));
            this.cacheMappings.put(path, cacheSeconds);
        }
    }

    /**
     * Map specific URL paths to a specific {@link com.rocket.summer.framework.http.CacheControl}.
     * <p>Overrides the default cache seconds setting of this interceptor.
     * Can specify a empty {@link com.rocket.summer.framework.http.CacheControl} instance
     * to exclude a URL path from default caching.
     * <p>Supports direct matches, e.g. a registered "/test" matches "/test",
     * and a various Ant-style pattern matches, e.g. a registered "/t*" matches
     * both "/test" and "/team". For details, see the AntPathMatcher javadoc.
     * <p><b>NOTE:</b> Path patterns are not supposed to overlap. If a request
     * matches several mappings, it is effectively undefined which one will apply
     * (due to the lack of key ordering in the underlying {@code java.util.HashMap}).
     * @param cacheControl the {@code CacheControl} to use
     * @param paths URL paths that will map to the given {@code CacheControl}
     * @since 4.2
     * @see #setCacheSeconds
     * @see com.rocket.summer.framework.util.AntPathMatcher
     */
    public void addCacheMapping(CacheControl cacheControl, String... paths) {
        for (String path : paths) {
            this.cacheControlMappings.put(path, cacheControl);
        }
    }

    /**
     * Set the PathMatcher implementation to use for matching URL paths
     * against registered URL patterns, for determining cache mappings.
     * Default is AntPathMatcher.
     * @see #addCacheMapping
     * @see #setCacheMappings
     * @see com.rocket.summer.framework.util.AntPathMatcher
     */
    public void setPathMatcher(PathMatcher pathMatcher) {
        Assert.notNull(pathMatcher, "PathMatcher must not be null");
        this.pathMatcher = pathMatcher;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws ServletException {

        checkRequest(request);

        String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
        if (logger.isDebugEnabled()) {
            logger.debug("Looking up cache seconds for [" + lookupPath + "]");
        }

        CacheControl cacheControl = lookupCacheControl(lookupPath);
        Integer cacheSeconds = lookupCacheSeconds(lookupPath);
        if (cacheControl != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Applying CacheControl to [" + lookupPath + "]");
            }
            applyCacheControl(response, cacheControl);
        }
        else if (cacheSeconds != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Applying CacheControl to [" + lookupPath + "]");
            }
            applyCacheSeconds(response, cacheSeconds);
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug("Applying default cache seconds to [" + lookupPath + "]");
            }
            prepareResponse(response);
        }

        return true;
    }

    /**
     * Look up a {@link com.rocket.summer.framework.http.CacheControl} instance for the given URL path.
     * <p>Supports direct matches, e.g. a registered "/test" matches "/test",
     * and various Ant-style pattern matches, e.g. a registered "/t*" matches
     * both "/test" and "/team". For details, see the AntPathMatcher class.
     * @param urlPath URL the bean is mapped to
     * @return the associated {@code CacheControl}, or {@code null} if not found
     * @see com.rocket.summer.framework.util.AntPathMatcher
     */
    protected CacheControl lookupCacheControl(String urlPath) {
        // Direct match?
        CacheControl cacheControl = this.cacheControlMappings.get(urlPath);
        if (cacheControl != null) {
            return cacheControl;
        }
        // Pattern match?
        for (String registeredPath : this.cacheControlMappings.keySet()) {
            if (this.pathMatcher.match(registeredPath, urlPath)) {
                return this.cacheControlMappings.get(registeredPath);
            }
        }
        return null;
    }

    /**
     * Look up a cacheSeconds integer value for the given URL path.
     * <p>Supports direct matches, e.g. a registered "/test" matches "/test",
     * and various Ant-style pattern matches, e.g. a registered "/t*" matches
     * both "/test" and "/team". For details, see the AntPathMatcher class.
     * @param urlPath URL the bean is mapped to
     * @return the cacheSeconds integer value, or {@code null} if not found
     * @see com.rocket.summer.framework.util.AntPathMatcher
     */
    protected Integer lookupCacheSeconds(String urlPath) {
        // Direct match?
        Integer cacheSeconds = this.cacheMappings.get(urlPath);
        if (cacheSeconds != null) {
            return cacheSeconds;
        }
        // Pattern match?
        for (String registeredPath : this.cacheMappings.keySet()) {
            if (this.pathMatcher.match(registeredPath, urlPath)) {
                return this.cacheMappings.get(registeredPath);
            }
        }
        return null;
    }


    /**
     * This implementation is empty.
     */
    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }

}
