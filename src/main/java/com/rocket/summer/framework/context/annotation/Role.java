package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.config.BeanDefinition;

import java.lang.annotation.*;

/**
 * Indicates the 'role' hint for a given bean.
 *
 * <p>May be used on any class directly or indirectly annotated with
 * {@link com.rocket.summer.framework.stereotype.Component} or on methods
 * annotated with {@link Bean}.
 *
 * <p>If this annotation is not present on a Component or Bean definition,
 * the default value of {@link BeanDefinition#ROLE_APPLICATION} will apply.
 *
 * <p>If Role is present on a {@link Configuration @Configuration} class,
 * this indicates the role of the configuration class bean definition and
 * does not cascade to all @{@code Bean} methods defined within. This behavior
 * is different than that of the @{@link Lazy} annotation, for example.
 *
 * @author Chris Beams
 * @since 3.1
 * @see BeanDefinition#ROLE_APPLICATION
 * @see BeanDefinition#ROLE_INFRASTRUCTURE
 * @see BeanDefinition#ROLE_SUPPORT
 * @see Bean
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Role {

    /**
     * Set the role hint for the associated bean.
     * @see BeanDefinition#ROLE_APPLICATION
     * @see BeanDefinition#ROLE_INFRASTRUCTURE
     * @see BeanDefinition#ROLE_SUPPORT
     */
    int value();

}
