package com.rocket.summer.framework.boot.autoconfigure.websocket;

import javax.servlet.Servlet;
import javax.websocket.server.ServerContainer;

import org.apache.catalina.startup.Tomcat;

import com.rocket.summer.framework.boot.autoconfigure.AutoConfigureBefore;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnClass;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnJava;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnJava.JavaVersion;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import com.rocket.summer.framework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;

/**
 * Auto configuration for websocket server in embedded Tomcat, Jetty or Undertow. Requires
 * the appropriate WebSocket modules to be on the classpath.
 * <p>
 * If Tomcat's WebSocket support is detected on the classpath we add a customizer that
 * installs the Tomcat Websocket initializer. In a non-embedded container it should
 * already be there.
 * <p>
 * If Jetty's WebSocket support is detected on the classpath we add a configuration that
 * configures the context with WebSocket support. In a non-embedded container it should
 * already be there.
 * <p>
 * If Undertow's WebSocket support is detected on the classpath we add a customizer that
 * installs the Undertow Websocket DeploymentInfo Customizer. In a non-embedded container
 * it should already be there.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
@Configuration
@ConditionalOnClass({ Servlet.class, ServerContainer.class })
@ConditionalOnWebApplication
@AutoConfigureBefore(EmbeddedServletContainerAutoConfiguration.class)
public class WebSocketAutoConfiguration {

    @Configuration
    @ConditionalOnClass(name = "org.apache.tomcat.websocket.server.WsSci",
            value = Tomcat.class)
    static class TomcatWebSocketConfiguration {

        @Bean
        @ConditionalOnMissingBean(name = "websocketContainerCustomizer")
        @ConditionalOnJava(JavaVersion.SEVEN)
        public TomcatWebSocketContainerCustomizer websocketContainerCustomizer() {
            return new TomcatWebSocketContainerCustomizer();
        }

    }

}
