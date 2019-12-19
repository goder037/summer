package com.rocket.summer.framework.web.cors;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.rocket.summer.framework.util.AntPathMatcher;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.PathMatcher;
import com.rocket.summer.framework.web.util.UrlPathHelper;

/**
 * Provide a per request {@link CorsConfiguration} instance based on a
 * collection of {@link CorsConfiguration} mapped on path patterns.
 *
 * <p>Exact path mapping URIs (such as {@code "/admin"}) are supported
 * as well as Ant-style path patterns (such as {@code "/admin/**"}).
 *
 * @author Sebastien Deleuze
 * @since 4.2
 */
public class UrlBasedCorsConfigurationSource implements CorsConfigurationSource {

    private final Map<String, CorsConfiguration> corsConfigurations = new LinkedHashMap<String, CorsConfiguration>();

    private PathMatcher pathMatcher = new AntPathMatcher();

    private UrlPathHelper urlPathHelper = new UrlPathHelper();


    /**
     * Set the PathMatcher implementation to use for matching URL paths
     * against registered URL patterns. Default is AntPathMatcher.
     * @see com.rocket.summer.framework.util.AntPathMatcher
     */
    public void setPathMatcher(PathMatcher pathMatcher) {
        Assert.notNull(pathMatcher, "PathMatcher must not be null");
        this.pathMatcher = pathMatcher;
    }

    /**
     * Set if URL lookup should always use the full path within the current servlet
     * context. Else, the path within the current servlet mapping is used if applicable
     * (that is, in the case of a ".../*" servlet mapping in web.xml).
     * <p>Default is "false".
     * @see com.rocket.summer.framework.web.util.UrlPathHelper#setAlwaysUseFullPath
     */
    public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
        this.urlPathHelper.setAlwaysUseFullPath(alwaysUseFullPath);
    }

    /**
     * Set if context path and request URI should be URL-decoded. Both are returned
     * <i>undecoded</i> by the Servlet API, in contrast to the servlet path.
     * <p>Uses either the request encoding or the default encoding according
     * to the Servlet spec (ISO-8859-1).
     * @see com.rocket.summer.framework.web.util.UrlPathHelper#setUrlDecode
     */
    public void setUrlDecode(boolean urlDecode) {
        this.urlPathHelper.setUrlDecode(urlDecode);
    }

    /**
     * Set if ";" (semicolon) content should be stripped from the request URI.
     * <p>The default value is {@code true}.
     * @see com.rocket.summer.framework.web.util.UrlPathHelper#setRemoveSemicolonContent(boolean)
     */
    public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
        this.urlPathHelper.setRemoveSemicolonContent(removeSemicolonContent);
    }

    /**
     * Set the UrlPathHelper to use for resolution of lookup paths.
     * <p>Use this to override the default UrlPathHelper with a custom subclass.
     */
    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
        this.urlPathHelper = urlPathHelper;
    }

    /**
     * Set CORS configuration based on URL patterns.
     */
    public void setCorsConfigurations(Map<String, CorsConfiguration> corsConfigurations) {
        this.corsConfigurations.clear();
        if (corsConfigurations != null) {
            this.corsConfigurations.putAll(corsConfigurations);
        }
    }

    /**
     * Get the CORS configuration.
     */
    public Map<String, CorsConfiguration> getCorsConfigurations() {
        return Collections.unmodifiableMap(this.corsConfigurations);
    }

    /**
     * Register a {@link CorsConfiguration} for the specified path pattern.
     */
    public void registerCorsConfiguration(String path, CorsConfiguration config) {
        this.corsConfigurations.put(path, config);
    }


    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
        for (Map.Entry<String, CorsConfiguration> entry : this.corsConfigurations.entrySet()) {
            if (this.pathMatcher.match(entry.getKey(), lookupPath)) {
                return entry.getValue();
            }
        }
        return null;
    }

}

