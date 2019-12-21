package com.rocket.summer.framework.boot.context.event;

import com.rocket.summer.framework.boot.SpringApplication;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.core.env.Environment;

/**
 * Event published as early as conceivably possible as soon as a {@link SpringApplication}
 * has been started - before the {@link Environment} or {@link ApplicationContext} is
 * available, but after the {@link ApplicationListener}s have been registered. The source
 * of the event is the {@link SpringApplication} itself, but beware of using its internal
 * state too much at this early stage since it might be modified later in the lifecycle.
 *
 * @author Dave Syer
 * @deprecated as of 1.5 in favor of {@link ApplicationStartingEvent}
 */
@Deprecated
@SuppressWarnings("serial")
public class ApplicationStartedEvent extends ApplicationStartingEvent {

    /**
     * Create a new {@link ApplicationStartedEvent} instance.
     * @param application the current application
     * @param args the arguments the application is running with
     */
    public ApplicationStartedEvent(SpringApplication application, String[] args) {
        super(application, args);
    }

}