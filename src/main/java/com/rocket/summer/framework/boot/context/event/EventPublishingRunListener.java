package com.rocket.summer.framework.boot.context.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rocket.summer.framework.boot.SpringApplication;
import com.rocket.summer.framework.boot.SpringApplicationRunListener;
import com.rocket.summer.framework.context.ApplicationContextAware;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.context.ConfigurableApplicationContext;
import com.rocket.summer.framework.context.event.ApplicationEventMulticaster;
import com.rocket.summer.framework.context.event.SimpleApplicationEventMulticaster;
import com.rocket.summer.framework.context.support.AbstractApplicationContext;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.core.env.ConfigurableEnvironment;
import com.rocket.summer.framework.util.ErrorHandler;

/**
 * {@link SpringApplicationRunListener} to publish {@link SpringApplicationEvent}s.
 * <p>
 * Uses an internal {@link ApplicationEventMulticaster} for the events that are fired
 * before the context is actually refreshed.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
public class EventPublishingRunListener implements SpringApplicationRunListener, Ordered {

    private final SpringApplication application;

    private final String[] args;

    private final SimpleApplicationEventMulticaster initialMulticaster;

    public EventPublishingRunListener(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
        this.initialMulticaster = new SimpleApplicationEventMulticaster();
        for (ApplicationListener<?> listener : application.getListeners()) {
            this.initialMulticaster.addApplicationListener(listener);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void starting() {
        this.initialMulticaster
                .multicastEvent(new ApplicationStartedEvent(this.application, this.args));
    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        this.initialMulticaster.multicastEvent(new ApplicationEnvironmentPreparedEvent(
                this.application, this.args, environment));
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {

    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        for (ApplicationListener<?> listener : this.application.getListeners()) {
            if (listener instanceof ApplicationContextAware) {
                ((ApplicationContextAware) listener).setApplicationContext(context);
            }
            context.addApplicationListener(listener);
        }
        this.initialMulticaster.multicastEvent(
                new ApplicationPreparedEvent(this.application, this.args, context));
    }

    @Override
    public void finished(ConfigurableApplicationContext context, Throwable exception) {
        SpringApplicationEvent event = getFinishedEvent(context, exception);
        if (context != null && context.isActive()) {
            // Listeners have been registered to the application context so we should
            // use it at this point if we can
            context.publishEvent(event);
        }
        else {
            // An inactive context may not have a multicaster so we use our multicaster to
            // call all of the context's listeners instead
            if (context instanceof AbstractApplicationContext) {
                for (ApplicationListener<?> listener : ((AbstractApplicationContext) context)
                        .getApplicationListeners()) {
                    this.initialMulticaster.addApplicationListener(listener);
                }
            }
            if (event instanceof ApplicationFailedEvent) {
                this.initialMulticaster.setErrorHandler(new LoggingErrorHandler());
            }
            this.initialMulticaster.multicastEvent(event);
        }
    }

    private SpringApplicationEvent getFinishedEvent(
            ConfigurableApplicationContext context, Throwable exception) {
        if (exception != null) {
            return new ApplicationFailedEvent(this.application, this.args, context,
                    exception);
        }
        return new ApplicationReadyEvent(this.application, this.args, context);
    }

    private static class LoggingErrorHandler implements ErrorHandler {

        private static Log logger = LogFactory.getLog(EventPublishingRunListener.class);

        @Override
        public void handleError(Throwable throwable) {
            logger.warn("Error calling ApplicationEventListener", throwable);
        }

    }

}

