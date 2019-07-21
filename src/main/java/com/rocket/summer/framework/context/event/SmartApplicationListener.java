package com.rocket.summer.framework.context.event;

import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.core.Ordered;

/**
 * Extended variant of the standard {@link ApplicationListener} interface,
 * exposing further metadata such as the supported event type.
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface SmartApplicationListener extends ApplicationListener<ApplicationEvent>, Ordered {

    /**
     * Determine whether this listener actually supports the given event type.
     */
    boolean supportsEventType(Class<? extends ApplicationEvent> eventType);

    /**
     * Determine whether this listener actually supports the given source type.
     */
    boolean supportsSourceType(Class<?> sourceType);

}

