package com.rocket.summer.framework.boot.context.embedded;

import com.rocket.summer.framework.context.event.ApplicationEvent;

/**
 * Event to be published after the context is refreshed and the
 * {@link EmbeddedServletContainer} is ready. Useful for obtaining the local port of a
 * running server. Normally it will have been started, but listeners are free to inspect
 * the server and stop and start it if they want to.
 *
 * @author Dave Syer
 */
public class EmbeddedServletContainerInitializedEvent extends ApplicationEvent {

    private final EmbeddedWebApplicationContext applicationContext;

    public EmbeddedServletContainerInitializedEvent(
            EmbeddedWebApplicationContext applicationContext,
            EmbeddedServletContainer source) {
        super(source);
        this.applicationContext = applicationContext;
    }

    /**
     * Access the {@link EmbeddedServletContainer}.
     * @return the embedded servlet container
     */
    public EmbeddedServletContainer getEmbeddedServletContainer() {
        return getSource();
    }

    /**
     * Access the source of the event (an {@link EmbeddedServletContainer}).
     * @return the embedded servlet container
     */
    @Override
    public EmbeddedServletContainer getSource() {
        return (EmbeddedServletContainer) super.getSource();
    }

    /**
     * Access the application context that the container was created in. Sometimes it is
     * prudent to check that this matches expectations (like being equal to the current
     * context) before acting on the server container itself.
     * @return the applicationContext that the container was created from
     */
    public EmbeddedWebApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

}

