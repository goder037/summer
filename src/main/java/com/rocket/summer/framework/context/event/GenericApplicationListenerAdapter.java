package com.rocket.summer.framework.context.event;

import com.rocket.summer.framework.aop.support.AopUtils;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.core.GenericTypeResolver;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.ResolvableType;
import com.rocket.summer.framework.util.Assert;

/**
 * {@link GenericApplicationListener} adapter that determines supported event types
 * through introspecting the generically declared type of the target listener.
 *
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 3.0
 * @see com.rocket.summer.framework.context.ApplicationListener#onApplicationEvent
 */
public class GenericApplicationListenerAdapter implements GenericApplicationListener, SmartApplicationListener {

    private final ApplicationListener<ApplicationEvent> delegate;

    private final ResolvableType declaredEventType;


    /**
     * Create a new GenericApplicationListener for the given delegate.
     * @param delegate the delegate listener to be invoked
     */
    @SuppressWarnings("unchecked")
    public GenericApplicationListenerAdapter(ApplicationListener<?> delegate) {
        Assert.notNull(delegate, "Delegate listener must not be null");
        this.delegate = (ApplicationListener<ApplicationEvent>) delegate;
        this.declaredEventType = resolveDeclaredEventType(this.delegate);
    }


    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        this.delegate.onApplicationEvent(event);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean supportsEventType(ResolvableType eventType) {
        if (this.delegate instanceof SmartApplicationListener) {
            Class<? extends ApplicationEvent> eventClass = (Class<? extends ApplicationEvent>) eventType.resolve();
            return (eventClass != null && ((SmartApplicationListener) this.delegate).supportsEventType(eventClass));
        }
        else {
            return (this.declaredEventType == null || this.declaredEventType.isAssignableFrom(eventType));
        }
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return supportsEventType(ResolvableType.forClass(eventType));
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return !(this.delegate instanceof SmartApplicationListener) ||
                ((SmartApplicationListener) this.delegate).supportsSourceType(sourceType);
    }

    @Override
    public int getOrder() {
        return (this.delegate instanceof Ordered ? ((Ordered) this.delegate).getOrder() : Ordered.LOWEST_PRECEDENCE);
    }


    static ResolvableType resolveDeclaredEventType(Class<?> listenerType) {
        ResolvableType resolvableType = ResolvableType.forClass(listenerType).as(ApplicationListener.class);
        return (resolvableType.hasGenerics() ? resolvableType.getGeneric() : null);
    }

    private static ResolvableType resolveDeclaredEventType(ApplicationListener<ApplicationEvent> listener) {
        ResolvableType declaredEventType = resolveDeclaredEventType(listener.getClass());
        if (declaredEventType == null || declaredEventType.isAssignableFrom(
                ResolvableType.forClass(ApplicationEvent.class))) {
            Class<?> targetClass = AopUtils.getTargetClass(listener);
            if (targetClass != listener.getClass()) {
                declaredEventType = resolveDeclaredEventType(targetClass);
            }
        }
        return declaredEventType;
    }

}
