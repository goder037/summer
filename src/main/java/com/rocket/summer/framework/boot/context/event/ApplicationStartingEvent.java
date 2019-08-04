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
 * @author Phillip Webb
 * @author Madhura Bhave
 * @since 1.5.0
 */
public class ApplicationStartingEvent extends SpringApplicationEvent {

    /**
     * Create a new {@link ApplicationStartingEvent} instance.
     * @param application the current application
     * @param args the arguments the application is running with
     */
    public ApplicationStartingEvent(SpringApplication application, String[] args) {
        super(application, args);
    }

}

