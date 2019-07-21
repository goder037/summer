package com.rocket.summer.framework.context.event;

import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.core.Ordered;

/**
 * {@link org.springframework.context.ApplicationListener} decorator that filters
 * events from a specified event source, invoking its delegate listener for
 * matching {@link org.springframework.context.ApplicationEvent} objects only.
 *
 * <p>Can also be used as base class, overriding the {@link #onApplicationEventInternal}
 * method instead of specifying a delegate listener.
 *
 * @author Juergen Hoeller
 * @since 2.0.5
 */
public class SourceFilteringListener implements SmartApplicationListener {

    private final Object source;

    private SmartApplicationListener delegate;


    /**
     * Create a SourceFilteringListener for the given event source.
     * @param source the event source that this listener filters for,
     * only processing events from this source
     * @param delegate the delegate listener to invoke with event
     * from the specified source
     */
    public SourceFilteringListener(Object source, ApplicationListener delegate) {
        this.source = source;
        this.delegate = (delegate instanceof SmartApplicationListener ?
                (SmartApplicationListener) delegate : new GenericApplicationListenerAdapter(delegate));
    }

    /**
     * Create a SourceFilteringListener for the given event source,
     * expecting subclasses to override the {@link #onApplicationEventInternal}
     * method (instead of specifying a delegate listener).
     * @param source the event source that this listener filters for,
     * only processing events from this source
     */
    protected SourceFilteringListener(Object source) {
        this.source = source;
    }


    public void onApplicationEvent(ApplicationEvent event) {
        if (event.getSource() == this.source) {
            onApplicationEventInternal(event);
        }
    }

    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return (this.delegate == null || this.delegate.supportsEventType(eventType));
    }

    public boolean supportsSourceType(Class<?> sourceType) {
        return sourceType.isInstance(this.source);
    }

    public int getOrder() {
        return (this.delegate != null ? this.delegate.getOrder() : Ordered.LOWEST_PRECEDENCE);
    }


    /**
     * Actually process the event, after having filtered according to the
     * desired event source already.
     * <p>The default implementation invokes the specified delegate, if any.
     * @param event the event to process (matching the specified source)
     */
    @SuppressWarnings("unchecked")
    protected void onApplicationEventInternal(ApplicationEvent event) {
        if (this.delegate == null) {
            throw new IllegalStateException(
                    "Must specify a delegate object or override the onApplicationEventInternal method");
        }
        this.delegate.onApplicationEvent(event);
    }

}

