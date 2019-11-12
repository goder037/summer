package com.rocket.summer.framework.jmx.export.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method-level annotation that indicates to expose a given bean property as a
 * JMX attribute, corresponding to the {@code ManagedAttribute} attribute.
 * Only valid when used on a JavaBean getter or setter.
 *
 * @author Rob Harrop
 * @since 1.2
 * @see com.rocket.summer.framework.jmx.export.metadata.ManagedAttribute
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ManagedAttribute {

    String defaultValue() default "";

    String description() default "";

    int currencyTimeLimit() default -1;

    String persistPolicy() default "";

    int persistPeriod() default -1;

}

