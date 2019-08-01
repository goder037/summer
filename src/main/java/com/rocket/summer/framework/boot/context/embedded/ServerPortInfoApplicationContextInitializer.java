package com.rocket.summer.framework.boot.context.embedded;

import com.rocket.summer.framework.beans.factory.annotation.Value;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.ApplicationListener;
import com.rocket.summer.framework.context.ConfigurableApplicationContext;
import com.rocket.summer.framework.core.env.*;
import com.rocket.summer.framework.util.StringUtils;
import com.rocket.summer.framework.context.ApplicationContextInitializer;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ApplicationContextInitializer} that sets {@link Environment} properties for the
 * ports that {@link EmbeddedServletContainer} servers are actually listening on. The
 * property {@literal "local.server.port"} can be injected directly into tests using
 * {@link Value @Value} or obtained via the {@link Environment}.
 * <p>
 * If the {@link EmbeddedWebApplicationContext} has a
 * {@link EmbeddedWebApplicationContext#setNamespace(String) namespace} set, it will be
 * used to construct the property name. For example, the "management" actuator context
 * will have the property name {@literal "local.management.port"}.
 * <p>
 * Properties are automatically propagated up to any parent context.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @since 1.4.0
 */
public class ServerPortInfoApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.addApplicationListener(
                new ApplicationListener<EmbeddedServletContainerInitializedEvent>() {

                    @Override
                    public void onApplicationEvent(
                            EmbeddedServletContainerInitializedEvent event) {
                        ServerPortInfoApplicationContextInitializer.this
                                .onApplicationEvent(event);
                    }

                });
    }

    protected void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
        String propertyName = getPropertyName(event.getApplicationContext());
        setPortProperty(event.getApplicationContext(), propertyName,
                event.getEmbeddedServletContainer().getPort());
    }

    protected String getPropertyName(EmbeddedWebApplicationContext context) {
        String name = context.getNamespace();
        if (StringUtils.isEmpty(name)) {
            name = "server";
        }
        return "local." + name + ".port";
    }

    private void setPortProperty(ApplicationContext context, String propertyName,
                                 int port) {
        if (context instanceof ConfigurableApplicationContext) {
            setPortProperty(((ConfigurableApplicationContext) context).getEnvironment(),
                    propertyName, port);
        }
        if (context.getParent() != null) {
            setPortProperty(context.getParent(), propertyName, port);
        }
    }

    @SuppressWarnings("unchecked")
    private void setPortProperty(ConfigurableEnvironment environment, String propertyName,
                                 int port) {
        MutablePropertySources sources = environment.getPropertySources();
        PropertySource<?> source = sources.get("server.ports");
        if (source == null) {
            source = new MapPropertySource("server.ports", new HashMap<String, Object>());
            sources.addFirst(source);
        }
        ((Map<String, Object>) source.getSource()).put(propertyName, port);
    }

}

