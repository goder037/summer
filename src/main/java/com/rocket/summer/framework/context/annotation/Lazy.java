package com.rocket.summer.framework.context.annotation;

import java.lang.annotation.*;

/**
 * Indicates whether a bean is to be lazily initialized.
 *
 * <p>May be used on any class directly or indirectly annotated with
 * {@link org.springframework.stereotype.Component} or on methods annotated with
 * {@link Bean}.
 *
 * <p>If this annotation is not present on a Component or Bean definition, eager
 * initialization will occur. If present and set to {@literal true}, the
 * Bean/Component will not be initialized until referenced by another bean or
 * explicitly retrieved from the enclosing
 * {@link org.springframework.beans.factory.BeanFactory}. If present and set to
 * {@literal false}, the bean will be instantiated on startup by bean factories
 * that perform eager initialization of singletons.
 *
 * <p>If Lazy is present on a {@link Configuration} class, this indicates that all
 * {@link Bean} methods within that {@literal Configuration} should be lazily
 * initialized. If Lazy is present and false on a Bean method within a
 * Lazy-annotated Configuration class, this indicates overriding the 'default
 * lazy' behavior and that the bean should be eagerly initialized.
 *
 * @author Chris Beams
 * @since 3.0
 * @see Primary
 * @see Bean
 * @see Configuration
 * @see org.springframework.stereotype.Component
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Lazy {

    /**
     * Whether lazy initialization should occur.
     */
    boolean value() default true;

}

