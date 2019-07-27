package com.rocket.summer.framework.web.context;

import com.rocket.summer.framework.core.env.ConfigurableEnvironment;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Specialization of {@link ConfigurableEnvironment} allowing initialization of
 * servlet-related {@link com.rocket.summer.framework.core.env.PropertySource} objects at the
 * earliest moment the {@link ServletContext} and (optionally) {@link ServletConfig}
 * become available.
 *
 * @author Chris Beams
 * @since 3.1.2
 * @see ConfigurableWebApplicationContext#getEnvironment()
 */
public interface ConfigurableWebEnvironment extends ConfigurableEnvironment {

    /**
     * Replace any {@linkplain
     * com.rocket.summer.framework.core.env.PropertySource.StubPropertySource stub property source}
     * instances acting as placeholders with real servlet context/config property sources
     * using the given parameters.
     * @param servletContext the {@link ServletContext} (may not be {@code null})
     * @param servletConfig the {@link ServletContext} ({@code null} if not available)
     */
    void initPropertySources(ServletContext servletContext, ServletConfig servletConfig);

}
