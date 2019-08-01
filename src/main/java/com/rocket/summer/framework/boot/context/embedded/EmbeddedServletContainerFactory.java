package com.rocket.summer.framework.boot.context.embedded;

import com.rocket.summer.framework.boot.web.servlet.ServletContextInitializer;
import org.apache.catalina.core.ApplicationContext;

/**
 * Factory interface that can be used to create {@link EmbeddedServletContainer}s.
 * Implementations are encouraged to extend
 * {@link AbstractEmbeddedServletContainerFactory} when possible.
 *
 * @author Phillip Webb
 * @see EmbeddedServletContainer
 * @see AbstractEmbeddedServletContainerFactory
 * @see TomcatEmbeddedServletContainerFactory
 */
public interface EmbeddedServletContainerFactory {

    /**
     * Gets a new fully configured but paused {@link EmbeddedServletContainer} instance.
     * Clients should not be able to connect to the returned server until
     * {@link EmbeddedServletContainer#start()} is called (which happens when the
     * {@link ApplicationContext} has been fully refreshed).
     * @param initializers {@link ServletContextInitializer}s that should be applied as
     * the container starts
     * @return a fully configured and started {@link EmbeddedServletContainer}
     * @see EmbeddedServletContainer#stop()
     */
    EmbeddedServletContainer getEmbeddedServletContainer(
            ServletContextInitializer... initializers);

}
