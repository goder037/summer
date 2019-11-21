package com.rocket.summer.framework.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define how Spring Data shall access values of persistent properties. Can either be {@link Type#FIELD}
 * or {@link Type#PROPERTY}. Default is field access.
 *
 * @author Oliver Gierke
 */
@Documented
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, })
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessType {

    /**
     * The access type to be used.
     *
     * @return
     */
    Type value();

    enum Type {
        FIELD, PROPERTY;
    }
}

