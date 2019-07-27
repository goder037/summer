package com.rocket.summer.framework.web.servlet.handler;

import com.rocket.summer.framework.beans.factory.BeanFactoryUtils;
import com.rocket.summer.framework.context.ApplicationContextException;
import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.util.ObjectUtils;

/**
 * Abstract implementation of the {@link com.rocket.summer.framework.web.servlet.HandlerMapping}
 * interface, detecting URL mappings for handler beans through introspection of all
 * defined beans in the application context.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see #determineUrlsForHandler
 */
public abstract class AbstractDetectingUrlHandlerMapping extends AbstractUrlHandlerMapping {

    private boolean detectHandlersInAncestorContexts = false;


    /**
     * Set whether to detect handler beans in ancestor ApplicationContexts.
     * <p>Default is "false": Only handler beans in the current ApplicationContext
     * will be detected, i.e. only in the context that this HandlerMapping itself
     * is defined in (typically the current DispatcherServlet's context).
     * <p>Switch this flag on to detect handler beans in ancestor contexts
     * (typically the Spring root WebApplicationContext) as well.
     */
    public void setDetectHandlersInAncestorContexts(boolean detectHandlersInAncestorContexts) {
        this.detectHandlersInAncestorContexts = detectHandlersInAncestorContexts;
    }


    /**
     * Calls the {@link #detectHandlers()} method in addition to the
     * superclass's initialization.
     */
    @Override
    public void initApplicationContext() throws ApplicationContextException {
        super.initApplicationContext();
        detectHandlers();
    }

    /**
     * Register all handlers found in the current ApplicationContext.
     * <p>The actual URL determination for a handler is up to the concrete
     * {@link #determineUrlsForHandler(String)} implementation. A bean for
     * which no such URLs could be determined is simply not considered a handler.
     * @throws com.rocket.summer.framework.beans.BeansException if the handler couldn't be registered
     * @see #determineUrlsForHandler(String)
     */
    protected void detectHandlers() throws BeansException {
        if (logger.isDebugEnabled()) {
            logger.debug("Looking for URL mappings in application context: " + getApplicationContext());
        }
        String[] beanNames = (this.detectHandlersInAncestorContexts ?
                BeanFactoryUtils.beanNamesForTypeIncludingAncestors(getApplicationContext(), Object.class) :
                getApplicationContext().getBeanNamesForType(Object.class));

        // Take any bean name that we can determine URLs for.
        for (String beanName : beanNames) {
            String[] urls = determineUrlsForHandler(beanName);
            if (!ObjectUtils.isEmpty(urls)) {
                // URL paths found: Let's consider it a handler.
                registerHandler(urls, beanName);
            }
            else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Rejected bean name '" + beanName + "': no URL paths identified");
                }
            }
        }
    }


    /**
     * Determine the URLs for the given handler bean.
     * @param beanName the name of the candidate bean
     * @return the URLs determined for the bean,
     * or <code>null</code> or an empty array if none
     */
    protected abstract String[] determineUrlsForHandler(String beanName);

}
