package com.rocket.summer.framework.data.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Demarcates a property to be used as version field to implement optimistic locking on entities.
 *
 * @since 1.5
 * @author Patryk Wasik
 * @author Oliver Gierke
 */
@Documented
@Retention(RUNTIME)
@Target(value = { FIELD, METHOD, ANNOTATION_TYPE })
public @interface Version {

}