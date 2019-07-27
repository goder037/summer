package com.rocket.summer.framework.web.context;

import com.rocket.summer.framework.context.ConfigurableApplicationContext;

/**
 * Callback interface for initializing a Spring {@link ConfigurableApplicationContext}
 * prior to being {@linkplain ConfigurableApplicationContext#refresh() refreshed}.
 *
 * <p>Typically used within web applications that require some programmatic initialization
 * of the application context. For example, registering property sources or activating
 * profiles against the {@linkplain ConfigurableApplicationContext#getEnvironment()
 * context's environment}. See {@code ContextLoader} and {@code FrameworkServlet} support
 * for declaring a "contextInitializerClasses" context-param and init-param, respectively.
 *
 * <p>{@code ApplicationContextInitializer} processors are encouraged to detect
 * whether Spring's {@link com.rocket.summer.framework.core.Ordered Ordered} interface has been
 * implemented or if the @{@link com.rocket.summer.framework.core.annotation.Order Order}
 * annotation is present and to sort instances accordingly if so prior to invocation.
 *
 * @author Chris Beams
 * @since 3.1
 * @see com.rocket.summer.framework.web.context.ContextLoader#customizeContext
 * @see com.rocket.summer.framework.web.context.ContextLoader#CONTEXT_INITIALIZER_CLASSES_PARAM
 * @see com.rocket.summer.framework.web.servlet.FrameworkServlet#setContextInitializerClasses
 * @see com.rocket.summer.framework.web.servlet.FrameworkServlet#applyInitializers
 */
public interface ApplicationContextInitializer<C extends ConfigurableApplicationContext> {

    /**
     * Initialize the given application context.
     * @param applicationContext the application to configure
     */
    void initialize(C applicationContext);

}
