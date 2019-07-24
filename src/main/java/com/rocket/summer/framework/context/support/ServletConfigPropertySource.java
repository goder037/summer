package com.rocket.summer.framework.context.support;

import com.rocket.summer.framework.core.env.EnumerablePropertySource;
import com.rocket.summer.framework.core.env.PropertySource;
import com.rocket.summer.framework.util.CollectionUtils;

import javax.servlet.ServletConfig;

/**
 * {@link PropertySource} that reads init parameters from a {@link ServletConfig} object.
 *
 * @author Chris Beams
 * @since 3.1
 * @see ServletContextPropertySource
 */
public class ServletConfigPropertySource extends EnumerablePropertySource<ServletConfig> {

    public ServletConfigPropertySource(String name, ServletConfig servletConfig) {
        super(name, servletConfig);
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
