package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;

/**
 * Indicates one or more {@link Configuration} classes to import.
 *
 * <p>Provides functionality equivalent to the {@literal <import/>} element in Spring XML.
 * Only supported for actual {@literal @Configuration}-annotated classes.
 *
 * <p>{@literal @Bean} definitions declared in imported {@literal @Configuration} classes
 * should be accessed by using {@link Autowired @Autowired} injection.  Either the bean
 * itself can be autowired, or the configuration class instance declaring the bean can be
 * autowired.  The latter approach allows for explicit, IDE-friendly navigation between
 * {@literal @Configuration} class methods.
 *
 * <p>If XML or other non-{@literal @Configuration} bean definition resources need to be
 * imported, use {@link ImportResource @ImportResource}
 *
 * @author Chris Beams
 * @since 3.0
 * @see Configuration
 * @see ImportResource
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {

    /**
     * The {@link Configuration} class or classes to import.
     */
    Class<?>[] value();
}