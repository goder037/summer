package com.rocket.summer.framework.web.context;

import com.rocket.summer.framework.context.ConfigurableApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Bootstrap listener to start up and shut down Spring's root {@link WebApplicationContext}.
 * Simply delegates to {@link ContextLoader} as well as to {@link ContextCleanupListener}.
 *
 * <p>This listener should be registered after {@link com.rocket.summer.framework.web.util.Log4jConfigListener}
 * in {@code web.xml}, if the latter is used.
 *
 * <p>As of Spring 3.1, {@code ContextLoaderListener} supports injecting the root web
 * application context via the {@link #ContextLoaderListener(WebApplicationContext)}
 * constructor, allowing for programmatic configuration in Servlet 3.0+ environments.
 * See {@link com.rocket.summer.framework.web.WebApplicationInitializer} for usage examples.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 17.02.2003
 * @see #setContextInitializers
 * @see com.rocket.summer.framework.web.WebApplicationInitializer
 * @see com.rocket.summer.framework.web.util.Log4jConfigListener
 */
public class ContextLoaderListener extends ContextLoader implements ServletContextListener {

    /**
     * Create a new {@code ContextLoaderListener} that will create a web application
     * context based on the "contextClass" and "contextConfigLocation" servlet
     * context-params. See {@link ContextLoader} superclass documentation for details on
     * default values for each.
     * <p>This constructor is typically used when declaring {@code ContextLoaderListener}
     * as a {@code <listener>} within {@code web.xml}, where a no-arg constructor is
     * required.
     * <p>The created application context will be registered into the ServletContext under
     * the attribute name {@link WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE}
     * and the Spring application context will be closed when the {@link #contextDestroyed}
     * lifecycle method is invoked on this listener.
     * @see ContextLoader
     * @see #ContextLoaderListener(WebApplicationContext)
     * @see #contextInitialized(ServletContextEvent)
     * @see #contextDestroyed(ServletContextEvent)
     */
    public ContextLoaderListener() {
    }

    /**
     * Create a new {@code ContextLoaderListener} with the given application context. This
     * constructor is useful in Servlet 3.0+ environments where instance-based
     * registration of listeners is possible through the {@link javax.servlet.ServletContext#addListener}
     * API.
     * <p>The context may or may not yet be {@linkplain
     * com.rocket.summer.framework.context.ConfigurableApplicationContext#refresh() refreshed}. If it
     * (a) is an implementation of {@link ConfigurableWebApplicationContext} and
     * (b) has <strong>not</strong> already been refreshed (the recommended approach),
     * then the following will occur:
     * <ul>
     * <li>If the given context has not already been assigned an {@linkplain
     * com.rocket.summer.framework.context.ConfigurableApplicationContext#setId id}, one will be assigned to it</li>
     * <li>{@code ServletContext} and {@code ServletConfig} objects will be delegated to
     * the application context</li>
     * <li>{@link #customizeContext} will be called</li>
     * <li>Any {@link com.rocket.summer.framework.context.ApplicationContextInitializer ApplicationContextInitializer}s
     * specified through the "contextInitializerClasses" init-param will be applied.</li>
     * <li>{@link com.rocket.summer.framework.context.ConfigurableApplicationContext#refresh refresh()} will be called</li>
     * </ul>
     * If the context has already been refreshed or does not implement
     * {@code ConfigurableWebApplicationContext}, none of the above will occur under the
     * assumption that the user has performed these actions (or not) per his or her
     * specific needs.
     * <p>See {@link com.rocket.summer.framework.web.WebApplicationInitializer} for usage examples.
     * <p>In any case, the given application context will be registered into the
     * ServletContext under the attribute name {@link
     * WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE} and the Spring
     * application context will be closed when the {@link #contextDestroyed} lifecycle
     * method is invoked on this listener.
     * @param context the application context to manage
     * @see #contextInitialized(ServletContextEvent)
     * @see #contextDestroyed(ServletContextEvent)
     */
    public ContextLoaderListener(WebApplicationContext context) {
        super(context);
    }


    /**
     * Initialize the root web application context.
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        initWebApplicationContext(event.getServletContext());
    }


    /**
     * Close the root web application context.
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        closeWebApplicationContext(event.getServletContext());
        ContextCleanupListener.cleanupAttributes(event.getServletContext());
    }

}