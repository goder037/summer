package com.rocket.summer.framework.data.keyvalue.annotation;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rocket.summer.framework.data.annotation.Persistent;

/**
 * Marker interface for methods with {@link Persistent} annotations indicating the presence of a dedicated keyspace the
 * entity should reside in. If present the value will be picked up for resolving the keyspace.
 *
 * <pre>
 * <code>
 * &#64;Persistent
 * &#64;Documented
 * &#64;Retention(RetentionPolicy.RUNTIME)
 * &#64;Target({ ElementType.TYPE })
 * public &#64;interface Document {
 *
 * 		&#64;KeySpace
 * 		String collection() default "person";
 * }
 * </code>
 * </pre>
 *
 * Can also be directly used on types to indicate the keyspace.
 *
 * <pre>
 * <code>
 * &#64;KeySpace("persons")
 * public class Foo {
 *
 * }
 * </code>
 * </pre>
 *
 * @author Christoph Strobl
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { METHOD, TYPE })
public @interface KeySpace {

    String value() default "";
}

