package com.rocket.summer.framework.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rocket.summer.framework.data.mapping.PersistentEntity;

/**
 * Annotation to allow {@link String} based type aliases to be used when writing type information for
 * {@link PersistentEntity}s.
 *
 * @author Oliver Gierke
 */
@Documented
@Inherited
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Persistent
public @interface TypeAlias {

    /**
     * The type alias to be used when persisting
     *
     * @return
     */
    String value();
}

