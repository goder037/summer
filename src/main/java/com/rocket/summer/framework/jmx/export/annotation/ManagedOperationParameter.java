package com.rocket.summer.framework.jmx.export.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method-level annotation used to provide metadata about operation parameters,
 * corresponding to a {@code ManagedOperationParameter} attribute.
 * Used as part of a {@link ManagedOperationParameters} annotation.
 *
 * <p>As of Spring Framework 4.2.4, this annotation is declared as repeatable.
 *
 * @author Rob Harrop
 * @since 1.2
 * @see ManagedOperationParameters#value
 * @see com.rocket.summer.framework.jmx.export.metadata.ManagedOperationParameter
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(ManagedOperationParameters.class)
public @interface ManagedOperationParameter {

    String name();

    String description();

}

