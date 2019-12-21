package com.rocket.summer.framework.boot.autoconfigure.info;

import java.io.IOException;
import java.util.Properties;

import com.rocket.summer.framework.boot.autoconfigure.EnableAutoConfiguration;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionMessage;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionOutcome;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnResource;
import com.rocket.summer.framework.boot.autoconfigure.condition.SpringBootCondition;
import com.rocket.summer.framework.boot.bind.RelaxedPropertyResolver;
import com.rocket.summer.framework.boot.context.properties.EnableConfigurationProperties;
import com.rocket.summer.framework.boot.info.BuildProperties;
import com.rocket.summer.framework.boot.info.GitProperties;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.ConditionContext;
import com.rocket.summer.framework.context.annotation.Conditional;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.core.env.PropertyResolver;
import com.rocket.summer.framework.core.io.DefaultResourceLoader;
import com.rocket.summer.framework.core.io.Resource;
import com.rocket.summer.framework.core.io.ResourceLoader;
import com.rocket.summer.framework.core.io.support.PropertiesLoaderUtils;
import com.rocket.summer.framework.core.type.AnnotatedTypeMetadata;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for various project information.
 *
 * @author Stephane Nicoll
 * @since 1.4.0
 */
@Configuration
@EnableConfigurationProperties(ProjectInfoProperties.class)
public class ProjectInfoAutoConfiguration {

    private final ProjectInfoProperties properties;

    public ProjectInfoAutoConfiguration(ProjectInfoProperties properties) {
        this.properties = properties;
    }

    @Conditional(GitResourceAvailableCondition.class)
    @ConditionalOnMissingBean
    @Bean
    public GitProperties gitProperties() throws Exception {
        return new GitProperties(loadFrom(this.properties.getGit().getLocation(), "git"));
    }

    @ConditionalOnResource(
            resources = "${spring.info.build.location:classpath:META-INF/build-info.properties}")
    @ConditionalOnMissingBean
    @Bean
    public BuildProperties buildProperties() throws Exception {
        return new BuildProperties(
                loadFrom(this.properties.getBuild().getLocation(), "build"));
    }

    protected Properties loadFrom(Resource location, String prefix) throws IOException {
        String p = (prefix.endsWith(".") ? prefix : prefix + ".");
        Properties source = PropertiesLoaderUtils.loadProperties(location);
        Properties target = new Properties();
        for (String key : source.stringPropertyNames()) {
            if (key.startsWith(p)) {
                target.put(key.substring(p.length()), source.get(key));
            }
        }
        return target;
    }

    static class GitResourceAvailableCondition extends SpringBootCondition {

        private final ResourceLoader defaultResourceLoader = new DefaultResourceLoader();

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context,
                                                AnnotatedTypeMetadata metadata) {
            ResourceLoader loader = context.getResourceLoader();
            if (loader == null) {
                loader = this.defaultResourceLoader;
            }
            PropertyResolver propertyResolver = context.getEnvironment();
            RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(
                    propertyResolver, "spring.info.git.");
            String location = resolver.getProperty("location");
            if (location == null) {
                resolver = new RelaxedPropertyResolver(propertyResolver, "spring.git.");
                location = resolver.getProperty("properties");
                if (location == null) {
                    location = "classpath:git.properties";
                }
            }
            ConditionMessage.Builder message = ConditionMessage
                    .forCondition("GitResource");
            if (loader.getResource(location).exists()) {
                return ConditionOutcome
                        .match(message.found("git info at").items(location));
            }
            return ConditionOutcome
                    .noMatch(message.didNotFind("git info at").items(location));
        }

    }

}

