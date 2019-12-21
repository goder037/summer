package com.rocket.summer.framework.context;

import com.rocket.summer.framework.context.event.ApplicationEvent;

/**
 * Interface that encapsulates event publication functionality.
 * Serves as super-interface for {@link ApplicationContext}.
 *
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 1.1.1
 * @see ApplicationContext
 * @see ApplicationEventPublisherAware
 * @see com.rocket.summer.framework.context.ApplicationEvent
 * @see com.rocket.summer.framework.context.event.EventPublicationInterceptor
 */
public interface ApplicationEventPublisher {

    /**
     * Notify all <strong>matching</strong> listeners registered with this
     * application of an application event. Events may be framework events
     * (such as RequestHandledEvent) or application-specific events.
     * @param event the event to publish
     * @see com.rocket.summer.framework.web.context.support.RequestHandledEvent
     */
    void publishEvent(ApplicationEvent event);

    /**
     * Notify all <strong>matching</strong> listeners registered with this
     * application of an event.
     * <p>If the specified {@code event} is not an {@link ApplicationEvent},
     * it is wrapped in a {@link PayloadApplicationEvent}.
     * @param event the event to publish
     * @since 4.2
     * @see PayloadApplicationEvent
     */
    void publishEvent(Object event);

}
