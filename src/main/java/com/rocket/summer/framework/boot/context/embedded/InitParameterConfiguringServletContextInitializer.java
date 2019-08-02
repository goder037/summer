package com.rocket.summer.framework.boot.context.embedded;

import com.rocket.summer.framework.boot.web.servlet.ServletContextInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Map;

/**
 * A {@code ServletContextInitializer} that configures init parameters on the
 * {@code ServletContext}.
 *
 * @author Andy Wilkinson
 * @since 1.2.0
 * @see ServletContext#setInitParameter(String, String)
 */
public class InitParameterConfiguringServletContextInitializer
        implements ServletContextInitializer {

    private final Map<String, String> parameters;

    public InitParameterConfiguringServletContextInitializer(
            Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
            servletContext.setInitParameter(entry.getKey(), entry.getValue());
        }
    }

}
