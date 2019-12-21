package com.rocket.summer.framework.boot.context.event;

import com.rocket.summer.framework.boot.SpringApplication;
import com.rocket.summer.framework.context.event.ApplicationEvent;

/**
 * Base class for {@link ApplicationEvent} related to a {@link SpringApplication}.
 *
 * @author Phillip Webb
 */
@SuppressWarnings("serial")
public abstract class SpringApplicationEvent extends ApplicationEvent {

    private final String[] args;

    public SpringApplicationEvent(SpringApplication application, String[] args) {
        super(application);
        this.args = args;
    }

    public SpringApplication getSpringApplication() {
        return (SpringApplication) getSource();
    }

    public final String[] getArgs() {
        return this.args;
    }

}