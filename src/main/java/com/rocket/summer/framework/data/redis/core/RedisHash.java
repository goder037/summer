package com.rocket.summer.framework.data.redis.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.rocket.summer.framework.core.annotation.AliasFor;
import com.rocket.summer.framework.data.annotation.Persistent;
import com.rocket.summer.framework.data.keyvalue.annotation.KeySpace;

/**
 * {@link RedisHash} marks Objects as aggregate roots to be stored in a Redis hash.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
@Persistent
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
@KeySpace
public @interface RedisHash {

    /**
     * The prefix to distinguish between domain types.
     *
     * @return
     * @see KeySpace
     */
    @AliasFor(annotation = KeySpace.class, attribute = "value")
    String value() default "";

    /**
     * Time before expire in seconds. Superseded by {@link TimeToLive}.
     *
     * @return positive number when expiration should be applied.
     */
    long timeToLive() default -1L;

}

