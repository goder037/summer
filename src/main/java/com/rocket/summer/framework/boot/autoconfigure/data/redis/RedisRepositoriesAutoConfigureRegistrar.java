package com.rocket.summer.framework.boot.autoconfigure.data.redis;

import java.lang.annotation.Annotation;

import com.rocket.summer.framework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import com.rocket.summer.framework.context.annotation.ImportBeanDefinitionRegistrar;
import com.rocket.summer.framework.data.redis.repository.configuration.EnableRedisRepositories;
import com.rocket.summer.framework.data.redis.repository.configuration.RedisRepositoryConfigurationExtension;
import com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtension;

/**
 * {@link ImportBeanDefinitionRegistrar} used to auto-configure Spring Data Redis
 * Repositories.
 *
 * @author Eddú Meléndez
 * @since 1.4.0
 */
class RedisRepositoriesAutoConfigureRegistrar
        extends AbstractRepositoryConfigurationSourceSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableRedisRepositories.class;
    }

    @Override
    protected Class<?> getConfiguration() {
        return EnableRedisRepositoriesConfiguration.class;
    }

    @Override
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new RedisRepositoryConfigurationExtension();
    }

    @EnableRedisRepositories
    private static class EnableRedisRepositoriesConfiguration {

    }

}

