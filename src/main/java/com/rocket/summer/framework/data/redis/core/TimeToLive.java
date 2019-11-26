package com.rocket.summer.framework.data.redis.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import com.rocket.summer.framework.data.annotation.ReadOnlyProperty;

/**
 * {@link TimeToLive} marks a single numeric property on aggregate root to be used for setting expirations in Redis. The
 * annotated property supersedes any other timeout configuration.
 *
 * <pre>
 * <code>
 * &#64;RedisHash
 * class Person {
 *   &#64;Id String id;
 *   String name;
 *   &#64;TimeToLive Long timeout;
 * }
 * </code>
 * </pre>
 *
 * @author Christoph Strobl
 * @since 1.7
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(value = { ElementType.FIELD, ElementType.METHOD })
@ReadOnlyProperty
public @interface TimeToLive {

    /**
     * {@link TimeUnit} unit to use.
     *
     * @return {@link TimeUnit#SECONDS} by default.
     */
    TimeUnit unit() default TimeUnit.SECONDS;
}

