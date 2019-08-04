package com.rocket.summer.framework.boot.context.event;

import com.rocket.summer.framework.boot.SpringApplication;
import com.rocket.summer.framework.context.ConfigurableApplicationContext;

/**
 * Event published as late as conceivably possible to indicate that the application is
 * ready to service requests. The source of the event is the {@link SpringApplication}
 * itself, but beware of modifying its internal state since all initialization steps will
 * have been completed by then.
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 * @see ApplicationFailedEvent
 */
@SuppressWarnings("serial")
public class ApplicationReadyEvent extends SpringApplicationEvent {

    private final ConfigurableApplicationContext context;

    /**
     * Create a new {@link ApplicationReadyEvent} instance.
     * @param application the current application
     * @param args the arguments the application is running with
     * @param context the context that was being created
     */
    public ApplicationReadyEvent(SpringApplication application, String[] args,
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
