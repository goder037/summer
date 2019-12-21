package com.rocket.summer.framework.boot.autoconfigure.context;

import com.rocket.summer.framework.boot.autoconfigure.EnableAutoConfiguration;
import com.rocket.summer.framework.boot.context.properties.ConfigurationProperties;
import com.rocket.summer.framework.boot.context.properties.EnableConfigurationProperties;
import com.rocket.summer.framework.context.annotation.Configuration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link ConfigurationProperties}
 * beans. Automatically binds and validates any bean annotated with
 * {@code @ConfigurationProperties}.
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 * @see EnableConfigurationProperties
 * @see ConfigurationProperties
 */
@Configuration
@EnableConfigurationProperties
public class ConfigurationPropertiesAutoConfiguration {

}

