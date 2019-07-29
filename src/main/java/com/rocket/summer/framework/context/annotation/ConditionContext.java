package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.config.ConfigurableListableBeanFactory;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.io.ResourceLoader;

/**
 * Context information for use by {@link Condition}s.
 *
 * @author Phillip Webb
 * @author Juergen Hoeller
 * @since 4.0
 */
public interface ConditionContext {

    /**
     * Return the {@link BeanDefinitionRegistry} that will hold the bean definition
     * should the condition match, or {@code null} if the registry is not available.
     */
    BeanDefinitionRegistry getRegistry();

    /**
     * Return the {@link ConfigurableListableBeanFactory} that will hold the bean
     * definition should the condition match, or {@code null} if the bean factory
     * is not available.
     */
    ConfigurableListableBeanFactory getBeanFactory();

    /**
     * Return the {@link Environment} for which the current application is running,
     * or {@code null} if no environment is available.
     */
    Environment getEnvironment();

    /**
     * Return the {@link ResourceLoader} currently being used, or {@code null} if
     * the resource loader cannot be obtained.
     */
    ResourceLoader getResourceLoader();

    /**
     * Return the {@link ClassLoader} that should be used to load additional classes,
     * or {@code null} if the default classloader should be used.
     */
    ClassLoader getClassLoader();

}
