package com.rocket.summer.framework.web.context;

import com.rocket.summer.framework.beans.factory.DisposableBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Enumeration;

/**
 * Web application listener that cleans up remaining disposable attributes
 * in the ServletContext, i.e. attributes which implement {@link DisposableBean}
 * and haven't been removed before. This is typically used for destroying objects
 * in "application" scope, for which the lifecycle implies destruction at the
 * very end of the web application's shutdown phase.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see com.rocket.summer.framework.web.context.support.ServletContextScope
 * @see ContextLoaderListener
 */
public class ContextCleanupListener implements ServletContextListener {

    private static final Log logger = LogFactory.getLog(ContextCleanupListener.class);


    public void contextInitialized(ServletContextEvent event) {
    }

    public void contextDestroyed(ServletContextEvent event) {
        cleanupAttributes(event.getServletContext());
    }


    /**
     * Find all ServletContext attributes which implement {@link DisposableBean}
     * and destroy them, removing all affected ServletContext attributes eventually.
     * @param sc the ServletContext to check
     */
    static void cleanupAttributes(ServletContext sc) {
        Enumeration attrNames = sc.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String attrName = (String) attrNames.nextElement();
            if (attrName.startsWith("com.rocket.summer.framework.")) {
                Object attrValue = sc.getAttribute(attrName);
                if (attrValue instanceof DisposableBean) {
                    try {
                        ((DisposableBean) attrValue).destroy();
                    }
                    catch (Throwable ex) {
                        logger.error("Couldn't invoke destroy method of attribute with name '" + attrName + "'", ex);
                    }
                }
            }
        }
    }

}

