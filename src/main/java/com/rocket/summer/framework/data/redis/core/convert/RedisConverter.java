package com.rocket.summer.framework.data.redis.core.convert;

import com.rocket.summer.framework.data.convert.EntityConverter;
import com.rocket.summer.framework.data.keyvalue.core.mapping.KeyValuePersistentEntity;
import com.rocket.summer.framework.data.keyvalue.core.mapping.KeyValuePersistentProperty;
import com.rocket.summer.framework.data.redis.core.mapping.RedisMappingContext;

/**
 * Redis specific {@link EntityConverter}.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public interface RedisConverter
        extends EntityConverter<KeyValuePersistentEntity<?>, KeyValuePersistentProperty, Object, RedisData> {

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.convert.EntityConverter#getMappingContext()
     */
    @Override
    RedisMappingContext getMappingContext();
}

