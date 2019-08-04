package com.rocket.summer.framework.boot.builder;

import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.ApplicationContextInitializer;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.context.ConfigurableApplicationContext;
import com.rocket.summer.framework.context.event.ApplicationEvent;
import com.rocket.summer.framework.context.event.ContextRefreshedEvent;
import com.rocket.summer.framework.core.Ordered;

/**
 * {@link ApplicationContextInitializer} for setting the parent context. Also publishes
 * {@link ParentContextAvailableEvent} when the context is refreshed to signal to other
 * listeners that the context is available and has a parent.
 *
 * @author Dave Syer
 */
public class ParentContextApplicationContextInitializer implements
        ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    private int order = Ordered.HIGHEST_PRECEDENCE;

    private final ApplicationContext parent;

    public ParentContextApplicationContextInitializer(ApplicationContext parent) {
        this.parent = parent;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (applicationContext != this.parent) {
            applicationContext.setParent(this.parent);
            applicationContext.addApplicationListener(EventPublisher.INSTANCE);
        }
    }

    private static class EventPublisher
            implements ApplicationListener<ContextRefreshedEvent>, Ordered {

        private static EventPublisher INSTANCE = new EventPublisher();

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            ApplicationContext context = event.getApplicationContext();
            if (context instanceof ConfigurableApplicationContext
                    && context == event.getSource()) {
                context.publishEvent(new ParentContextAvailableEvent(
                        (ConfigurableApplicationContext) context));
            }
        }

    }

    /**
     * {@link ApplicationEvent} fired when a parent context is available.
     */
    @SuppressWarnings("serial")
    public static class ParentContextAvailableEvent extends ApplicationEvent {

        public ParentContextAvailableEvent(
                ConfigurableApplicationContext applicationContext) {
            super(applicationContext);
        }

        public ConfigurableApplicationContext getApplicationContext() {
            return (ConfigurableApplicationContext) getSource();
        }

    }

}

