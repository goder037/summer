package com.rocket.summer.framework.jmx.export.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method-level annotation that indicates to expose a given method as a
 * JMX operation, corresponding to the {@code ManagedOperation} attribute.
 * Only valid when used on a method that is not a JavaBean getter or setter.
 *
 * @author Rob Harrop
 * @since 1.2
 * @see com.rocket.summer.framework.jmx.export.metadata.ManagedOperation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ManagedOperation {

    String description() default "";

    int currencyTimeLimit() default -1;

}

