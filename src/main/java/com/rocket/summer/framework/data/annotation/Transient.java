package com.rocket.summer.framework.data.annotation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field to be transient for the mapping framework. Thus the property will not be persisted and not further
 * inspected by the mapping framework.
 *
 * @author Oliver Gierke
 * @author Jon Brisbin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { FIELD, METHOD, ANNOTATION_TYPE })
public @interface Transient {
}
