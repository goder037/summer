package com.rocket.summer.framework.context.annotation;

import com.rocket.summer.framework.beans.factory.annotation.Autowired;
import com.rocket.summer.framework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Indicates that a class declares one or more {@link Bean} methods and may be processed
 * by the Spring container to generate bean definitions and service requests for those beans
 * at runtime.
 *
 * <p>Configuration is meta-annotated as a {@link Component}, therefore Configuration
 * classes are candidates for component-scanning and may also take advantage of
 * {@link Autowired} at the field and method but not at the constructor level.
 * Externalized values may be wired into Configuration classes using the {@link Value}
 * annotation.
 *
 * <p>May be used in conjunction with the {@link Lazy} annotation to indicate that all Bean
 * methods declared within this class are by default lazily initialized.
 *
 * <h3>Constraints</h3>
 * <ul>
 *    <li>Configuration classes must be non-final
 *    <li>Configuration classes must be non-local (may not be declared within a method)
 *    <li>Configuration classes must have a default/no-arg constructor and may not use
 *        {@link Autowired} constructor parameters
 * </ul>
 *
 * @author Rod Johnson
 * @author Chris Beams
 * @since 3.0
 * @see Import
 * @see Lazy
 * @see Bean
 * @see ConfigurationClassPostProcessor
 * @see AnnotationConfigApplicationContext
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {

    /**
     * Explicitly specify the name of the Spring bean definition associated
     * with this Configuration class.  If left unspecified (the common case),
     * a bean name will be automatically generated.
     *
     * <p>The custom name applies only if the Configuration class is picked up via
     * component scanning or supplied directly to a {@link AnnotationConfigApplicationContext}.
     * If the Configuration class is registered as a traditional XML bean definition,
     * the name/id of the bean element will take precedence.
     *
     * @return the specified bean name, if any
     * @see org.springframework.beans.factory.support.DefaultBeanNameGenerator
     */
    String value() default "";

}

