package com.rocket.summer.framework.context;

import com.rocket.summer.framework.context.event.ApplicationEvent;

/**
 * Interface that encapsulates event publication functionality.
 * Serves as super-interface for ApplicationContext.
 *
 * @author Juergen Hoeller
 * @since 1.1.1
 * @see ApplicationContext
 * @see ApplicationEventPublisherAware
 * @see org.springframework.context.ApplicationEvent
 * @see org.springframework.context.event.EventPublicationInterceptor
 */
public interface ApplicationEventPublisher {

    /**
     * Notify all listeners registered with this application of an application
     * event. Events may be framework events (such as RequestHandledEvent)
     * or application-specific events.
     * @param event the event to publish
     * @see org.springframework.web.context.support.RequestHandledEvent
     */
    void publishEvent(ApplicationEvent event);

}