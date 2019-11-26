package com.rocket.summer.framework.data.redis.repository.configuration;

import java.lang.annotation.Annotation;

import com.rocket.summer.framework.context.annotation.ImportBeanDefinitionRegistrar;
import com.rocket.summer.framework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import com.rocket.summer.framework.data.repository.config.RepositoryConfigurationExtension;

/**
 * Redis specific {@link ImportBeanDefinitionRegistrar}.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public class RedisRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport#getAnnotation()
     */
    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableRedisRepositories.class;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport#getExtension()
     */
    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new RedisRepositoryConfigurationExtension();
    }
}

