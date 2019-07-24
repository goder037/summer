package com.rocket.summer.framework.context.support;

import com.rocket.summer.framework.core.env.EnumerablePropertySource;
import com.rocket.summer.framework.core.env.PropertySource;
import com.rocket.summer.framework.util.CollectionUtils;

import javax.servlet.ServletContext;

/**
 * {@link PropertySource} that reads init parameters from a {@link ServletContext} object.
 *
 * @author Chris Beams
 * @since 3.1
 * @see ServletConfigPropertySource
 */
public class ServletContextPropertySource extends EnumerablePropertySource<ServletContext> {

    public ServletContextPropertySource(String name, ServletContext servletContext) {
        super(name, servletContext);
    }

    @Override
    public String[] getPropertyNames() {
        return CollectionUtils.toArray(
                this.source.getInitParameterNames(), EMPTY_NAMES_ARRAY);
    }

    @Override
    public String getProperty(String name) {
        return this.source.getInitParameter(name);
    }
}

