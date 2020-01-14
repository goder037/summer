package com.rocket.summer.framework.web.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rocket.summer.framework.context.ApplicationContextInitializer;
import com.rocket.summer.framework.web.WebApplicationInitializer;

/**
 * Convenient base class for {@link WebApplicationInitializer} implementations
 * that register a {@link ContextLoaderListener} in the servlet context.
 *
 * <p>The only method required to be implemented by subclasses is
 * {@link #createRootApplicationContext()}, which gets invoked from
 * {@link #registerContextLoaderListener(ServletContext)}.
 *
 * @author Arjen Poutsma
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.2
 */
public abstract class AbstractContextLoaderInitializer implements WebApplicationInitializer {

    /** Logger available to subclasses */
    protected final Log logger = LogFactory.getLog(getClass());


    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        registerContextLoaderListener(servletContext);
    }

    /**
     * Register a {@link ContextLoaderListener} against the given servlet context. The
     * {@code ContextLoaderListener} is initialized with the application context returned
     * from the {@link #createRootApplicationContext()} template method.
     * @param servletContext the servlet context to register the listener against
     */
    protected void registerContextLoaderListener(ServletContext servletContext) {
        WebApplicationContext rootAppContext = createRootApplicationContext();
        if (rootAppContext != null) {
            ContextLoaderListener listener = new ContextLoaderListener(rootAppContext);
            listener.setContextInitializers(getRootApplicationContextInitializers());
            servletContext.addListener(listener);
        }
        else {
            logger.debug("No ContextLoaderListener registered, as " +
                    "createRootApplicationContext() did not return an application context");
        }
    }

    /**
     * Create the "<strong>root</strong>" application context to be provided to the
     * {@code ContextLoaderListener}.
     * <p>The returned context is delegated to
     * {@link ContextLoaderListener#ContextLoaderListener(WebApplicationContext)} and will
     * be established as the parent context for any {@code DispatcherServlet} application
     * contexts. As such, it typically contains middle-tier services, data sources, etc.
     * @return the root application context, or {@code null} if a root context is not
     * desired
     * @see com.rocket.summer.framework.web.servlet.support.AbstractDispatcherServletInitializer
     */
    protected abstract WebApplicationContext createRootApplicationContext();

    /**
     * Specify application context initializers to be applied to the root application
     * context that the {@code ContextLoaderListener} is being created with.
     * @since 4.2
     * @see #createRootApplicationContext()
     * @see ContextLoaderListener#setContextInitializers
     */
    protected ApplicationContextInitializer<?>[] getRootApplicationContextInitializers() {
        return null;
    }

}

