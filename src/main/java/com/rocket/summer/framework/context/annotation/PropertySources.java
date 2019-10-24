package com.rocket.summer.framework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation that aggregates several {@link PropertySource} annotations.
 *
 * <p>Can be used natively, declaring several nested {@link PropertySource} annotations.
 * Can also be used in conjunction with Java 8's support for <em>repeatable annotations</em>,
 * where {@link PropertySource} can simply be declared several times on the same
 * {@linkplain ElementType#TYPE type}, implicitly generating this container annotation.
 *
 * @author Phillip Webb
 * @since 4.0
 * @see PropertySource
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PropertySources {

    PropertySource[] value();

}
