package com.rocket.summer.framework.web.context;

import javax.servlet.ServletContext;

/**
 * Interface to be implemented by any object that wishes to be notified
 * of the ServletContext (typically determined by the WebApplicationContext)
 * that it runs in.
 *
 * @author Juergen Hoeller
 * @since 12.03.2004
 * @see ServletConfigAware
 */
public interface ServletContextAware {

    /**
     * Set the ServletContext that this object runs in.
     * <p>Invoked after population of normal bean properties but before an init
     * callback like InitializingBean's <code>afterPropertiesSet</code> or a
     * custom init-method. Invoked after ApplicationContextAware's
     * <code>setApplicationContext</code>.
     * @param servletContext ServletContext object to be used by this object
     * @see com.rocket.summer.framework.beans.factory.InitializingBean#afterPropertiesSet
     * @see com.rocket.summer.framework.context.ApplicationContextAware#setApplicationContext
     */
    void setServletContext(ServletContext servletContext);

}
