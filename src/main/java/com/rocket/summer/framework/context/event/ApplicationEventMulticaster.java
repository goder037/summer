package com.rocket.summer.framework.context.event;

import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.core.ResolvableType;

/**
 * Interface to be implemented by objects that can manage a number of
 * {@link ApplicationListener} objects, and publish events to them.
 *
 * <p>An {@link com.rocket.summer.framework.context.ApplicationEventPublisher}, typically
 * a Spring {@link com.rocket.summer.framework.context.ApplicationContext}, can use an
 * ApplicationEventMulticaster as a delegate for actually publishing events.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 */
public interface ApplicationEventMulticaster {

    /**
     * Add a listener to be notified of all events.
     * @param listener the listener to add
     */
    void addApplicationListener(ApplicationListener<?> listener);

    /**
     * Add a listener bean to be notified of all events.
     * @param listenerBeanName the name of the listener bean to add
     */
    void addApplicationListenerBean(String listenerBeanName);

    /**
     * Remove a listener from the notification list.
     * @param listener the listener to remove
     */
    void removeApplicationListener(ApplicationListener<?> listener);

    /**
     * Remove a listener bean from the notification list.
     * @param listenerBeanName the name of the listener bean to add
     */
    void removeApplicationListenerBean(String listenerBeanName);

    /**
     * Remove all listeners registered with this multicaster.
     * <p>After a remove call, the multicaster will perform no action
     * on event notification until new listeners are being registered.
     */
    void removeAllListeners();

    /**
     * Multicast the given application event to appropriate listeners.
     * <p>Consider using {@link #multicastEvent(ApplicationEvent, ResolvableType)}
     * if possible as it provides a better support for generics-based events.
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
