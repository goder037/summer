package com.rocket.summer.framework.context.event;

import com.rocket.summer.framework.context.ApplicationContext;

/**
 * Event raised when an <code>ApplicationContext</code> gets initialized or refreshed.
 *
 * @author Juergen Hoeller
 * @since 04.03.2003
 * @see ContextClosedEvent
 */
public class ContextRefreshedEvent extends ApplicationContextEvent {

    /**
     * Create a new ContextRefreshedEvent.
     * @param source the <code>ApplicationContext</code> that has been initialized
     * or refreshed (must not be <code>null</code>)
     */
    public ContextRefreshedEvent(ApplicationContext source) {
        super(source);
    }

}
