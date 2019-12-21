package com.rocket.summer.framework.boot.context.embedded.tomcat;

import org.apache.catalina.connector.Connector;

/**
 * Callback interface that can be used to customize a Tomcat {@link Connector}.
 *
 * @author Dave Syer
 * @see TomcatEmbeddedServletContainerFactory
 */
public interface TomcatConnectorCustomizer {

    /**
     * Customize the connector.
     * @param connector the connector to customize
     */
    void customize(Connector connector);

}

