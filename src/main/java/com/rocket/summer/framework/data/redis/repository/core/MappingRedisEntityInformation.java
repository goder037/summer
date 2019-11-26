package com.rocket.summer.framework.data.redis.repository.core;

import java.io.Serializable;

import com.rocket.summer.framework.data.mapping.model.MappingException;
import com.rocket.summer.framework.data.redis.core.mapping.RedisPersistentEntity;
import com.rocket.summer.framework.data.repository.core.support.PersistentEntityInformation;

/**
 * {@link RedisEntityInformation} implementation using a {@link MongoPersistentEntity} instance to lookup the necessary
 * information. Can be configured with a custom collection to be returned which will trump the one returned by the
 * {@link MongoPersistentEntity} if given.
 *
 * @author Christoph Strobl
 * @param <T>
 * @param <ID>
 */
public class MappingRedisEntityInformation<T, ID extends Serializable>
        extends PersistentEntityInformation<T, Serializable> implements RedisEntityInformation<T, Serializable> {

    private final RedisPersistentEntity<T> entityMetadata;

    /**
     * @param entity
     */
    public MappingRedisEntityInformation(RedisPersistentEntity<T> entity) {
        super(entity);

        this.entityMetadata = entity;

        if (!entityMetadata.hasIdProperty()) {

            throw new MappingException(
                    String.format("Entity %s requires to have an explicit id field. Did you forget to provide one using @Id?",
                            entity.getName()));
        }
    }
}


