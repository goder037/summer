package com.rocket.summer.framework.lang;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element uses the Http Server available in
 * {@code com.sun.*} classes, which is only available on a Sun/Oracle JVM.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
@Documented
public @interface UsesSunHttpServer {
}

