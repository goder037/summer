package com.rocket.summer.framework.boot.context.embedded.tomcat;

import org.apache.catalina.Context;

/**
 * Callback interface that can be used to customize a Tomcat {@link Context}.
 *
 * @author Dave Syer
 * @see TomcatEmbeddedServletContainerFactory
 */
public interface TomcatContextCustomizer {

    /**
     * Customize the context.
     * @param context the context to customize
     */
    void customize(Context context);

}

