package com.rocket.summer.framework.boot.context.embedded.tomcat;

import com.rocket.summer.framework.boot.context.embedded.EmbeddedServletContainerException;
import org.apache.catalina.connector.Connector;

/**
 * A {@code ConnectorStartFailedException} is thrown when a Tomcat {@link Connector} fails
 * to start, for example due to a port clash or incorrect SSL configuration.
 *
 * @author Andy Wilkinson
 * @since 1.4.1
 */
public class ConnectorStartFailedException extends EmbeddedServletContainerException {

    private final int port;

    /**
     * Creates a new {@code ConnectorStartFailedException} for a connector that's
     * configured to listen on the given {@code port}.
     * @param port the port
     */
    public ConnectorStartFailedException(int port) {
        super("Connector configured to listen on port " + port + " failed to start",
                null);
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

}
