package com.rocket.summer.framework.boot.context.event;

import com.rocket.summer.framework.boot.SpringApplication;
import com.rocket.summer.framework.core.env.ConfigurableEnvironment;

/**
 * Event published when a {@link SpringApplication} is starting up and the
 * {@link Environment} is first available for inspection and modification.
 *
 * @author Dave Syer
 */
@SuppressWarnings("serial")
public class ApplicationEnvironmentPreparedEvent extends SpringApplicationEvent {

    private final ConfigurableEnvironment environment;

    /**
     * Create a new {@link ApplicationEnvironmentPreparedEvent} instance.
     * @param application the current application
     * @param args the arguments the application is running with
     * @param environment the environment that was just created
     */
    public ApplicationEnvironmentPreparedEvent(SpringApplication application,
                                               String[] args, ConfigurableEnvironment environment) {
        super(application, args);
        this.environment = environment;
    }

    /**
     * Return the environment.
     * @return the environment
     */
    public ConfigurableEnvironment getEnvironment() {
        return this.environment;
    }

}
