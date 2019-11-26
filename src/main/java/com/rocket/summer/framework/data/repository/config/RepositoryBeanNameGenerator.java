package com.rocket.summer.framework.data.repository.config;

import com.rocket.summer.framework.beans.factory.BeanClassLoaderAware;
import com.rocket.summer.framework.beans.factory.annotation.AnnotatedBeanDefinition;
import com.rocket.summer.framework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import com.rocket.summer.framework.beans.factory.config.BeanDefinition;
import com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry;
import com.rocket.summer.framework.beans.factory.support.BeanNameGenerator;
import com.rocket.summer.framework.context.annotation.AnnotationBeanNameGenerator;
import com.rocket.summer.framework.util.ClassUtils;

/**
 * Special {@link BeanNameGenerator} to create bean names for Spring Data repositories. Will delegate to an
 * {@link AnnotationBeanNameGenerator} but let the delegate work with a customized {@link BeanDefinition} to make sure
 * the repository interface is inspected and not the actual bean definition class.
 *
 * @author Oliver Gierke
 */
public class RepositoryBeanNameGenerator implements BeanNameGenerator, BeanClassLoaderAware {

    private static final BeanNameGenerator DELEGATE = new AnnotationBeanNameGenerator();

    private ClassLoader beanClassLoader;

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.lang.ClassLoader)
     */
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.beans.factory.support.BeanNameGenerator#generateBeanName(com.rocket.summer.framework.beans.factory.config.BeanDefinition, com.rocket.summer.framework.beans.factory.support.BeanDefinitionRegistry)
     */
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {

        AnnotatedBeanDefinition beanDefinition = definition instanceof AnnotatedBeanDefinition //
                ? (AnnotatedBeanDefinition) definition //
                : new AnnotatedGenericBeanDefinition(getRepositoryInterfaceFrom(definition));

        return DELEGATE.generateBeanName(beanDefinition, registry);
    }

    /**
     * Returns the type configured for the {@code repositoryInterface} constructor argument of the given bean definition.
     * Uses a potential {@link Class} being configured as is or tries to load a class with the given value's
     * {@link #toString()} representation.
     *
     * @param beanDefinition
     * @return
     */
    private Class<?> getRepositoryInterfaceFrom(BeanDefinition beanDefinition) {

        Object value = beanDefinition.getConstructorArgumentValues().getArgumentValue(0, Class.class).getValue();

        if (value instanceof Class<?>) {
            return (Class<?>) value;
        } else {
            try {
                return ClassUtils.forName(value.toString(), beanClassLoader);
            } catch (Exception o_O) {
                throw new RuntimeException(o_O);
            }
        }
    }
}

