package com.rocket.summer.framework.web.context.support;

import com.rocket.summer.framework.core.io.DefaultResourceLoader;
import com.rocket.summer.framework.core.io.Resource;

import javax.servlet.ServletContext;

/**
 * ResourceLoader implementation that resolves paths as ServletContext
 * resources, for use outside a WebApplicationContext (for example,
 * in an HttpServletBean or GenericFilterBean subclass).
 *
 * <p>Within a WebApplicationContext, resource paths are automatically
 * resolved as ServletContext resources by the context implementation.
 *
 * @author Juergen Hoeller
 * @since 1.0.2
 * @see #getResourceByPath
 * @see ServletContextResource
 * @see com.rocket.summer.framework.web.context.WebApplicationContext
 * @see com.rocket.summer.framework.web.servlet.HttpServletBean
 * @see com.rocket.summer.framework.web.filter.GenericFilterBean
 */
public class ServletContextResourceLoader extends DefaultResourceLoader {

    private final ServletContext servletContext;


    /**
     * Create a new ServletContextResourceLoader.
     * @param servletContext the ServletContext to load resources with
     */
    public ServletContextResourceLoader(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * This implementation supports file paths beneath the root of the web application.
     * @see ServletContextResource
     */
    @Override
    protected Resource getResourceByPath(String path) {
        return new ServletContextResource(this.servletContext, path);
    }

}
