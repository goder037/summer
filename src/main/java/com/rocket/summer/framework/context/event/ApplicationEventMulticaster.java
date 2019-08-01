package com.rocket.summer.framework.context.event;

import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.core.ResolvableType;

/**
 * Interface to be implemented by objects that can manage a number
 * of ApplicationListeners, and publish events to them. An example
 * of such an object is an ApplicationEventPublisher, typically
 * the ApplicationContext, which can use an ApplicationEventMulticaster
 * as a helper to publish events to listeners.
 *
 * @author Rod Johnson
 */
public interface ApplicationEventMulticaster {

    /**
     * Add a listener to be notified of all events.
     * @param listener the listener to add
     */
    void addApplicationListener(ApplicationListener listener);

    /**
     * Remove a listener from the notification list.
     * @param listener the listener to remove
     */
    void removeApplicationListener(ApplicationListener listener);

    /**
     * Remove all listeners registered with this multicaster.
     * It will perform no action on event notification until more
     * listeners are registered.
     */
    void removeAllListeners();

    /**
     * Multicast the given application event to appropriate listeners.
     * @param event the event to multicast
     */
    void multicastEvent(ApplicationEvent event);


    /**
     * Multicast the given application event to appropriate listeners.
     * <p>If the {@code eventType} is {@code null}, a default type is built
     * based on the {@code event} instance.
     * @param event the event to multicast
     * @param eventType the type of event (can be null)
     * @since 4.2
     */
    void multicastEvent(ApplicationEvent event, ResolvableType eventType);

}
