package com.rocket.summer.framework.web.context.support;

import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.web.context.ServletContextAware;
import com.rocket.summer.framework.web.context.WebApplicationContext;
import com.rocket.summer.framework.web.util.WebUtils;

import javax.servlet.ServletContext;
import java.io.File;

/**
 * Convenient superclass for application objects running in a WebApplicationContext.
 * Provides <code>getWebApplicationContext()</code>, <code>getServletContext()</code>,
 * and <code>getTempDir()</code> methods.
 *
 * @author Juergen Hoeller
 * @since 28.08.2003
 * @see SpringBeanAutowiringSupport
 */
public abstract class WebApplicationObjectSupport extends ApplicationObjectSupport
        implements ServletContextAware {

    private ServletContext servletContext;


    public final void setServletContext(ServletContext servletContext) {
        if (servletContext != this.servletContext) {
            this.servletContext = servletContext;
            if (servletContext != null) {
                initServletContext(servletContext);
            }
        }
    }

    /**
     * Overrides the base class behavior to enforce running in an ApplicationContext.
     * All accessors will throw IllegalStateException if not running in a context.
     * @see #getApplicationContext()
     * @see #getMessageSourceAccessor()
     * @see #getWebApplicationContext()
     * @see #getServletContext()
     * @see #getTempDir()
     */
    @Override
    protected boolean isContextRequired() {
        return true;
    }

    /**
     * Calls {@link #initServletContext(javax.servlet.ServletContext)} if the
     * given ApplicationContext is a {@link WebApplicationContext}.
     */
    @Override
    protected void initApplicationContext(ApplicationContext context) {
        super.initApplicationContext(context);
        if (this.servletContext == null && context instanceof WebApplicationContext) {
            this.servletContext = ((WebApplicationContext) context).getServletContext();
            if (this.servletContext != null) {
                initServletContext(this.servletContext);
            }
        }
    }

    /**
     * Subclasses may override this for custom initialization based
     * on the ServletContext that this application object runs in.
     * <p>The default implementation is empty. Called by
     * {@link #initApplicationContext(com.rocket.summer.framework.context.ApplicationContext)}
     * as well as {@link #setServletContext(javax.servlet.ServletContext)}.
     * @param servletContext the ServletContext that this application object runs in
     * (never <code>null</code>)
     */
    protected void initServletContext(ServletContext servletContext) {
    }

    /**
     * Return the current application context as WebApplicationContext.
     * <p><b>NOTE:</b> Only use this if you actually need to access
     * WebApplicationContext-specific functionality. Preferably use
     * <code>getApplicationContext()</code> or <code>getServletContext()</code>
     * else, to be able to run in non-WebApplicationContext environments as well.
     * @throws IllegalStateException if not running in a WebApplicationContext
     * @see #getApplicationContext()
     */
    protected final WebApplicationContext getWebApplicationContext() throws IllegalStateException {
        ApplicationContext ctx = getApplicationContext();
        if (ctx instanceof WebApplicationContext) {
            return (WebApplicationContext) getApplicationContext();
        }
        else if (isContextRequired()) {
            throw new IllegalStateException("WebApplicationObjectSupport instance [" + this +
                    "] does not run in a WebApplicationContext but in: " + ctx);
        }
        else {
            return null;
        }
    }

    /**
     * Return the current ServletContext.
     * @throws IllegalStateException if not running within a ServletContext
     */
    protected final ServletContext getServletContext() throws IllegalStateException {
        if (this.servletContext != null) {
            return this.servletContext;
        }
        ServletContext servletContext = getWebApplicationContext().getServletContext();
        if (servletContext == null && isContextRequired()) {
            throw new IllegalStateException("WebApplicationObjectSupport instance [" + this +
                    "] does not run within a ServletContext. Make sure the object is fully configured!");
        }
        return servletContext;
    }

    /**
     * Return the temporary directory for the current web application,
     * as provided by the servlet container.
     * @return the File representing the temporary directory
     * @throws IllegalStateException if not running within a ServletContext
     * @see com.rocket.summer.framework.web.util.WebUtils#getTempDir(javax.servlet.ServletContext)
     */
    protected final File getTempDir() throws IllegalStateException {
        return WebUtils.getTempDir(getServletContext());
    }

}
