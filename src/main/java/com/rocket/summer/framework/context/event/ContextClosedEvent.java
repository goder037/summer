package com.rocket.summer.framework.context.event;

import com.rocket.summer.framework.context.ApplicationContext;

/**
 * Event raised when an <code>ApplicationContext</code> gets closed.
 *
 * @author Juergen Hoeller
 * @since 12.08.2003
 * @see ContextRefreshedEvent
 */
public class ContextClosedEvent extends ApplicationContextEvent {

    /**
     * Creates a new ContextClosedEvent.
     * @param source the <code>ApplicationContext</code> that has been closed
     * (must not be <code>null</code>)
     */
    public ContextClosedEvent(ApplicationContext source) {
        super(source);
    }

}