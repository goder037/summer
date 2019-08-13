package com.rocket.summer.framework.boot.autoconfigure.web;

import com.rocket.summer.framework.context.BeansException;
import com.rocket.summer.framework.boot.autoconfigure.EnableAutoConfiguration;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import com.rocket.summer.framework.boot.autoconfigure.condition.SearchStrategy;
import com.rocket.summer.framework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import com.rocket.summer.framework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import com.rocket.summer.framework.boot.context.properties.EnableConfigurationProperties;
import com.rocket.summer.framework.context.ApplicationContext;
import com.rocket.summer.framework.context.ApplicationContextAware;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.core.Ordered;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;

/**
 * {@link EnableAutoConfiguration Auto-configuration} that configures the
 * {@link ConfigurableEmbeddedServletContainer} from a {@link ServerProperties} bean.
 *
 * @author Dave Syer
 * @author Andy Wilkinson
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnWebApplication
public class ServerPropertiesAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
    public ServerProperties serverProperties() {
        return new ServerProperties();
    }

    @Bean
    public DuplicateServerPropertiesDetector duplicateServerPropertiesDetector() {
        return new DuplicateServerPropertiesDetector();
    }

    /**
     * {@link EmbeddedServletContainerCustomizer} that ensures there is exactly one
     * {@link ServerProperties} bean in the application context.
     */
    private static class DuplicateServerPropertiesDetector implements
            EmbeddedServletContainerCustomizer, Ordered, ApplicationContextAware {

        private ApplicationContext applicationContext;

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext)
                throws BeansException {
            this.applicationContext = applicationContext;
        }

        @Override
        public void customize(ConfigurableEmbeddedServletContainer container) {
            // ServerProperties handles customization, this just checks we only have
            // a single bean
            String[] serverPropertiesBeans = this.applicationContext
                    .getBeanNamesForType(ServerProperties.class);
            Assert.state(serverPropertiesBeans.length != 0,
                    "No ServerProperties registered");
            Assert.state(serverPropertiesBeans.length == 1,
                    "Multiple ServerProperties registered " + StringUtils
                            .arrayToCommaDelimitedString(serverPropertiesBeans));
        }

    }

}

