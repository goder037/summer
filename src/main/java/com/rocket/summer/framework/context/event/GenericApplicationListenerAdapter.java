package com.rocket.summer.framework.context.event;

import com.rocket.summer.framework.aop.support.AopUtils;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.core.GenericTypeResolver;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.util.Assert;

/**
 * {@link SmartApplicationListener} adapter that determines supported event types
 * through introspecting the generically declared type of the target listener.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see org.springframework.context.ApplicationListener#onApplicationEvent
 */
public class GenericApplicationListenerAdapter implements SmartApplicationListener {

    private final ApplicationListener delegate;


    /**
     * Create a new GenericApplicationListener for the given delegate.
     * @param delegate the delegate listener to be invoked
     */
    public GenericApplicationListenerAdapter(ApplicationListener delegate) {
        Assert.notNull(delegate, "Delegate listener must not be null");
        this.delegate = delegate;
    }


    @SuppressWarnings("unchecked")
    public void onApplicationEvent(ApplicationEvent event) {
        this.delegate.onApplicationEvent(event);
    }

    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        Class typeArg = GenericTypeResolver.resolveTypeArgument(this.delegate.getClass(), ApplicationListener.class);
        if (typeArg == null || typeArg.equals(ApplicationEvent.class)) {
            Class targetClass = AopUtils.getTargetClass(this.delegate);
            if (targetClass != this.delegate.getClass()) {
                typeArg = GenericTypeResolver.resolveTypeArgument(targetClass, ApplicationListener.class);
            }
        }
        return (typeArg == null || typeArg.isAssignableFrom(eventType));
    }

    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    public int getOrder() {
        return (this.delegate instanceof Ordered ? ((Ordered) this.delegate).getOrder() : Ordered.LOWEST_PRECEDENCE);
    }

}

