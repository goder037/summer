package com.rocket.summer.framework.data.redis.core.index;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark properties value to be included in a secondary index. <br />
 * Uses Redis {@literal GEO} structures for storage. <br />
 * The value will be part of the key built for the index.
 *
 * @author Christoph Strobl
 * @since 1.8
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
public @interface GeoIndexed {

}
