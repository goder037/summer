package com.rocket.summer.framework.data.redis.core.convert;

import java.io.Serializable;
import java.util.Map;

import com.rocket.summer.framework.data.annotation.Reference;

/**
 * {@link ReferenceResolver} retrieves Objects marked with {@link Reference} from Redis.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public interface ReferenceResolver {

    /**
     * @param id must not be {@literal null}.
     * @param keyspace must not be {@literal null}.
     * @return {@literal null} if referenced object does not exist.
     */
    Map<byte[], byte[]> resolveReference(Serializable id, String keyspace);
}

