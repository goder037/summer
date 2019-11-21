package com.rocket.summer.framework.data.annotation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field to be {@literal read-only} for the mapping framework and therefore will not be persisted.
 *
 * @author Christoph Strobl
 * @since 1.9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { FIELD, METHOD, ANNOTATION_TYPE })
public @interface ReadOnlyProperty {
}