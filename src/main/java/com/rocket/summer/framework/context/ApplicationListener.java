package com.rocket.summer.framework.context;

import com.rocket.summer.framework.context.event.ApplicationEvent;

import java.util.EventListener;

/**
 * Interface to be implemented by application event listeners.
 * Based on the standard <code>java.util.EventListener</code> interface
 * for the Observer design pattern.
 *
 * @author Rod Johnson
 * @see org.springframework.context.event.ApplicationEventMulticaster
 */
public interface ApplicationListener extends EventListener {

    /**
     * Handle an application event.
     * @param event the event to respond to
     */
    void onApplicationEvent(ApplicationEvent event);

}
