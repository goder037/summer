package com.rocket.summer.framework.context.event;

import com.rocket.summer.framework.context.ApplicationContext;

/**
 * Event raised when an <code>ApplicationContext</code> gets stopped.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 2.5
 * @see ContextStartedEvent
 */
public class ContextStoppedEvent extends ApplicationContextEvent {

    /**
     * Create a new ContextStoppedEvent.
     * @param source the <code>ApplicationContext</code> that has been stopped
     * (must not be <code>null</code>)
     */
    public ContextStoppedEvent(ApplicationContext source) {
        super(source);
    }

}
