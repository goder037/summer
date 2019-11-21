package com.rocket.summer.framework.boot.autoconfigure.dao;

import com.rocket.summer.framework.boot.autoconfigure.EnableAutoConfiguration;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnClass;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import com.rocket.summer.framework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.rocket.summer.framework.boot.bind.RelaxedPropertyResolver;
import com.rocket.summer.framework.context.annotation.Bean;
import com.rocket.summer.framework.context.annotation.Configuration;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.dao.annotation.PersistenceExceptionTranslationPostProcessor;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring's persistence exception
 * translation.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @since 1.2.0
 */
@Configuration
@ConditionalOnClass(PersistenceExceptionTranslationPostProcessor.class)
public class PersistenceExceptionTranslationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(PersistenceExceptionTranslationPostProcessor.class)
    @ConditionalOnProperty(prefix = "spring.dao.exceptiontranslation", name = "enabled",
            matchIfMissing = true)
    public static PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor(
            Environment environment) {
        PersistenceExceptionTranslationPostProcessor postProcessor = new PersistenceExceptionTranslationPostProcessor();
        postProcessor.setProxyTargetClass(determineProxyTargetClass(environment));
        return postProcessor;
    }

    private static boolean determineProxyTargetClass(Environment environment) {
        RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(environment,
                "spring.aop.");
        Boolean value = resolver.getProperty("proxyTargetClass", Boolean.class);
        return (value != null) ? value : true;
    }

}

