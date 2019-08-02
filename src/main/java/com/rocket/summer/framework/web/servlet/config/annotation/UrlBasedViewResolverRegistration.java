package com.rocket.summer.framework.web.servlet.config.annotation;

import com.rocket.summer.framework.web.servlet.view.UrlBasedViewResolver;

import java.util.Map;

/**
 * Assist with configuring a {@link com.rocket.summer.framework.web.servlet.view.UrlBasedViewResolver}.
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class UrlBasedViewResolverRegistration {

    protected final UrlBasedViewResolver viewResolver;


    public UrlBasedViewResolverRegistration(UrlBasedViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }


    protected UrlBasedViewResolver getViewResolver() {
        return this.viewResolver;
    }

    /**
     * Set the prefix that gets prepended to view names when building a URL.
     * @see com.rocket.summer.framework.web.servlet.view.UrlBasedViewResolver#setPrefix
     */
    public UrlBasedViewResolverRegistration prefix(String prefix) {
        this.viewResolver.setPrefix(prefix);
        return this;
    }

    /**
     * Set the suffix that gets appended to view names when building a URL.
     * @see com.rocket.summer.framework.web.servlet.view.UrlBasedViewResolver#setSuffix
     */
    public UrlBasedViewResolverRegistration suffix(String suffix) {
        this.viewResolver.setSuffix(suffix);
        return this;
    }

    /**
     * Set the view class that should be used to create views.
     * @see com.rocket.summer.framework.web.servlet.view.UrlBasedViewResolver#setViewClass
     */
    public UrlBasedViewResolverRegistration viewClass(Class<?> viewClass) {
        this.viewResolver.setViewClass(viewClass);
        return this;
    }

    /**
     * Set the view names (or name patterns) that can be handled by this view
     * resolver. View names can contain simple wildcards such that 'my*', '*Report'
     * and '*Repo*' will all match the view name 'myReport'.
     * @see com.rocket.summer.framework.web.servlet.view.UrlBasedViewResolver#setViewNames
     */
    public UrlBasedViewResolverRegistration viewNames(String... viewNames) {
        this.viewResolver.setViewNames(viewNames);
        return this;
    }

    /**
     * Set static attributes to be added to the model of every request for all
     * views resolved by this view resolver. This allows for setting any kind of
     * attribute values, for example bean references.
     * @see com.rocket.summer.framework.web.servlet.view.UrlBasedViewResolver#setAttributesMap
     */
    public UrlBasedViewResolverRegistration attributes(Map<String, ?> attributes) {
        this.viewResolver.setAttributesMap(attributes);
        return this;
    }

    /**
     * Specify the maximum number of entries for the view cache.
     * Default is 1024.
     * @see com.rocket.summer.framework.web.servlet.view.UrlBasedViewResolver#setCache(boolean)
     */
    public UrlBasedViewResolverRegistration cacheLimit(int cacheLimit) {
        this.viewResolver.setCacheLimit(cacheLimit);
        return this;
    }

    /**
     * Enable or disable caching.
     * <p>This is equivalent to setting the {@link #cacheLimit "cacheLimit"}
     * property to the default limit (1024) or to 0, respectively.
     * <p>Default is "true": caching is enabled.
     * Disable this only for debugging and development.
     * @see com.rocket.summer.framework.web.servlet.view.UrlBasedViewResolver#setCache(boolean)
     */
    public UrlBasedViewResolverRegistration cache(boolean cache) {
        this.viewResolver.setCache(cache);
        return this;
    }

}



