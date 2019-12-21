package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.Aware;
import com.rocket.summer.framework.beans.factory.BeanClassLoaderAware;
import com.rocket.summer.framework.beans.factory.BeanFactory;
import com.rocket.summer.framework.beans.factory.BeanFactoryAware;
import com.rocket.summer.framework.beans.factory.config.ConfigurableBeanFactory;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.context.EnvironmentAware;
import com.rocket.summer.framework.context.ResourceLoaderAware;
import com.rocket.summer.framework.core.env.Environment;
import com.rocket.summer.framework.core.io.ResourceLoader;

/**
 * Common delegate code for the handling of parser strategies, e.g.
 * {@code TypeFilter}, {@code ImportSelector}, {@code ImportBeanDefinitionRegistrar}
 *
 * @author Juergen Hoeller
 * @since 4.3.3
 */
abstract class ParserStrategyUtils {

    /**
     * Invoke {@link BeanClassLoaderAware}, {@link BeanFactoryAware},
     * {@link EnvironmentAware}, and {@link ResourceLoaderAware} contracts
     * if implemented by the given object.
     */
    public static void invokeAwareMethods(Object parserStrategyBean, Environment environment,
                                          ResourceLoader resourceLoader, BeanDefinitionRegistry registry) {

        if (parserStrategyBean instanceof Aware) {
            if (parserStrategyBean instanceof BeanClassLoaderAware) {
                ClassLoader classLoader = (registry instanceof ConfigurableBeanFactory ?
                        ((ConfigurableBeanFactory) registry).getBeanClassLoader() : resourceLoader.getClassLoader());
                ((BeanClassLoaderAware) parserStrategyBean).setBeanClassLoader(classLoader);
            }
            if (parserStrategyBean instanceof BeanFactoryAware && registry instanceof BeanFactory) {
                ((BeanFactoryAware) parserStrategyBean).setBeanFactory((BeanFactory) registry);
            }
            if (parserStrategyBean instanceof EnvironmentAware) {
                ((EnvironmentAware) parserStrategyBean).setEnvironment(environment);
            }
            if (parserStrategyBean instanceof ResourceLoaderAware) {
                ((ResourceLoaderAware) parserStrategyBean).setResourceLoader(resourceLoader);
            }
        }
    }

}

