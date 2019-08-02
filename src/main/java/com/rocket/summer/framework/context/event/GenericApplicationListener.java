package com.rocket.summer.framework.context.event;

import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.ResolvableType;

/**
 * Extended variant of the standard {@link ApplicationListener} interface,
 * exposing further metadata such as the supported event and source type.
 *
 * <p>As of Spring Framework 4.2, this interface supersedes the Class-based
 * {@link SmartApplicationListener} with full handling of generic event types.
 *
 * @author Stephane Nicoll
 * @since 4.2
 * @see SmartApplicationListener
 * @see GenericApplicationListenerAdapter
 */
public interface GenericApplicationListener extends ApplicationListener<ApplicationEvent>, Ordered {

    /**
     * Determine whether this listener actually supports the given event type.
     * @param eventType the event type (never {@code null})
     */
    boolean supportsEventType(ResolvableType eventType);

    /**
     * Determine whether this listener actually supports the given source type.
     * @param sourceType the source type, or {@code null} if no source
     */
    boolean supportsSourceType(Class<?> sourceType);

}
