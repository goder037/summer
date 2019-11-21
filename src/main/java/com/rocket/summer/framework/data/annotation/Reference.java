package com.rocket.summer.framework.data.annotation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-annotation to be used to annotate annotations that mark references to other objects.
 *
 * @author Oliver Gierke
 * @author Jon Brisbin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { FIELD, METHOD, ANNOTATION_TYPE })
public @interface Reference {
}
