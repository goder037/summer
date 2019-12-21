package com.rocket.summer.framework.context.event;

import com.rocket.summer.framework.context.ApplicationContext;

/**
 * Event raised when an <code>ApplicationContext</code> gets started.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 2.5
 * @see ContextStoppedEvent
 */
public class ContextStartedEvent extends ApplicationContextEvent {

    /**
     * Create a new ContextStartedEvent.
     * @param source the <code>ApplicationContext</code> that has been started
     * (must not be <code>null</code>)
     */
    public ContextStartedEvent(ApplicationContext source) {
        super(source);
    }

}
