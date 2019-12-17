package com.rocket.summer.framework.objenesis.instantiator.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denote that the class in an instantiator of a given type
 *
 * @author Henri Tremblay
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Instantiator {

    /**
     * @return type of instantiator
     */
    Typology value();
}