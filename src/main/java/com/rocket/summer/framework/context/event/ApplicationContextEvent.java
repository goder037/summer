package com.rocket.summer.framework.context.event;

import com.rocket.summer.framework.context.ApplicationContext;

/**
 * Base class for events raised for an <code>ApplicationContext</code>.
 *
 * @author Juergen Hoeller
 * @since 2.5
 */
public abstract class ApplicationContextEvent extends ApplicationEvent {

    /**
     * Create a new ContextStartedEvent.
     * @param source the <code>ApplicationContext</code> that the event is raised for
     * (must not be <code>null</code>)
     */
    public ApplicationContextEvent(ApplicationContext source) {
        super(source);
    }

    /**
     * Get the <code>ApplicationContext</code> that the event was raised for.
     */
    public final ApplicationContext getApplicationContext() {
        return (ApplicationContext) getSource();
    }

}
