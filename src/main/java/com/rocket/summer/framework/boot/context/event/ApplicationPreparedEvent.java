package com.rocket.summer.framework.boot.context.event;

import com.rocket.summer.framework.boot.SpringApplication;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.ConfigurableApplicationContext;
import com.rocket.summer.framework.core.env.Environment;

/**
 * Event published as when a {@link SpringApplication} is starting up and the
 * {@link ApplicationContext} is fully prepared but not refreshed. The bean definitions
 * will be loaded and the {@link Environment} is ready for use at this stage.
 *
 * @author Dave Syer
 */
@SuppressWarnings("serial")
public class ApplicationPreparedEvent extends SpringApplicationEvent {

    private final ConfigurableApplicationContext context;

    /**
     * Create a new {@link ApplicationPreparedEvent} instance.
     * @param application the current application
     * @param args the arguments the application is running with
     * @param context the ApplicationContext about to be refreshed
     */
    public ApplicationPreparedEvent(SpringApplication application, String[] args,
                                    ConfigurableApplicationContext context) {
        super(application, args);
        this.context = context;
    }

    /**
     * Return the application context.
     * @return the context
     */
    public ConfigurableApplicationContext getApplicationContext() {
        return this.context;
    }

}
