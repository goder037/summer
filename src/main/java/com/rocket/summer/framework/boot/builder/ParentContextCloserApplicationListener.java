package com.rocket.summer.framework.boot.builder;

import com.rocket.summer.framework.context.*;
import com.rocket.summer.framework.context.event.ContextClosedEvent;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.util.ObjectUtils;
import com.rocket.summer.framework.boot.builder.ParentContextApplicationContextInitializer.ParentContextAvailableEvent;

import java.lang.ref.WeakReference;

/**
 * Listener that closes the application context if its parent is closed. It listens for
 * refresh events and grabs the current context from there, and then listens for closed
 * events and propagates it down the hierarchy.
 *
 * @author Dave Syer
 * @author Eric Bottard
 */
public class ParentContextCloserApplicationListener
        implements ApplicationListener<ParentContextAvailableEvent>,
        ApplicationContextAware, Ordered {

    private int order = Ordered.LOWEST_PRECEDENCE - 10;

    private ApplicationContext context;

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void onApplicationEvent(ParentContextAvailableEvent event) {
        maybeInstallListenerInParent(event.getApplicationContext());
    }

    private void maybeInstallListenerInParent(ConfigurableApplicationContext child) {
        if (child == this.context) {
            if (child.getParent() instanceof ConfigurableApplicationContext) {
                ConfigurableApplicationContext parent = (ConfigurableApplicationContext) child
                        .getParent();
                parent.addApplicationListener(createContextCloserListener(child));
            }
        }
    }

    /**
     * Subclasses may override to create their own subclass of ContextCloserListener. This
     * still enforces the use of a weak reference.
     * @param child the child context
     * @return the {@link ContextCloserListener} to use
     */
    protected ContextCloserListener createContextCloserListener(
            ConfigurableApplicationContext child) {
        return new ContextCloserListener(child);
    }

    /**
     * {@link ApplicationListener} to close the context.
     */
    protected static class ContextCloserListener
            implements ApplicationListener<ContextClosedEvent> {

        private WeakReference<ConfigurableApplicationContext> childContext;

        public ContextCloserListener(ConfigurableApplicationContext childContext) {
            this.childContext = new WeakReference<ConfigurableApplicationContext>(
                    childContext);
        }

        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            ConfigurableApplicationContext context = this.childContext.get();
            if ((context != null)
                    && (event.getApplicationContext() == context.getParent())
                    && context.isActive()) {
                context.close();
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (obj instanceof ContextCloserListener) {
                ContextCloserListener other = (ContextCloserListener) obj;
                return ObjectUtils.nullSafeEquals(this.childContext.get(),
                        other.childContext.get());
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.childContext.get());
        }

    }

}
