package com.rocket.summer.framework.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element uses Java 8 specific API constructs,
 * without implying that it strictly requires Java 8.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
@Documented
public @interface UsesJava8 {
}

