package com.rocket.summer.framework.boot.autoconfigure.context;

import com.rocket.summer.framework.boot.autoconfigure.AutoConfigureOrder;
import com.rocket.summer.framework.boot.autoconfigure.EnableAutoConfiguration;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.SearchStrategy;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.context.support.PropertySourcesPlaceholderConfigurer;
import com.rocket.summer.framework.core.Ordered;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for
 * {@link PropertySourcesPlaceholderConfigurer}.
 *
 * @author Phillip Webb
 * @author Dave Syer
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class PropertyPlaceholderAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
