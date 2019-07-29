package com.rocket.summer.framework.boot;

import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.ConfigurableApplicationContext;
import com.rocket.summer.framework.core.env.ConfigurableEnvironment;

/**
 * Listener for the {@link SpringApplication} {@code run} method.
 * {@link SpringApplicationRunListener}s are loaded via the {@link SpringFactoriesLoader}
 * and should declare a public constructor that accepts a {@link SpringApplication}
 * instance and a {@code String[]} of arguments. A new
 * {@link SpringApplicationRunListener} instance will be created for each run.
 *
 * @author Phillip Webb
 * @author Dave Syer
 */
public interface SpringApplicationRunListener {

    /**
     * Called immediately when the run method has first started. Can be used for very
     * early initialization.
     */
    void starting();

    /**
     * Called once the environment has been prepared, but before the
     * {@link ApplicationContext} has been created.
     * @param environment the environment
     */
    void environmentPrepared(ConfigurableEnvironment environment);

    /**
     * Called once the {@link ApplicationContext} has been created and prepared, but
     * before sources have been loaded.
     * @param context the application context
     */
    void contextPrepared(ConfigurableApplicationContext context);

    /**
     * Called once the application context has been loaded but before it has been
     * refreshed.
     * @param context the application context
     */
    void contextLoaded(ConfigurableApplicationContext context);

    /**
     * Called immediately before the run method finishes.
     * @param context the application context or null if a failure occurred before the
     * context was created
     * @param exception any run exception or null if run completed successfully.
     */
    void finished(ConfigurableApplicationContext context, Throwable exception);

}