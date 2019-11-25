package com.rocket.summer.framework.data.redis.repository.support;

import java.io.Serializable;

import com.rocket.summer.framework.data.keyvalue.core.KeyValueOperations;
import com.rocket.summer.framework.data.keyvalue.repository.query.KeyValuePartTreeQuery;
import com.rocket.summer.framework.data.keyvalue.repository.support.KeyValueRepositoryFactory;
import com.rocket.summer.framework.data.redis.core.mapping.RedisPersistentEntity;
import com.rocket.summer.framework.data.redis.repository.core.MappingRedisEntityInformation;
import com.rocket.summer.framework.data.redis.repository.query.RedisQueryCreator;
import com.rocket.summer.framework.data.repository.core.EntityInformation;
import com.rocket.summer.framework.data.repository.core.support.RepositoryFactorySupport;
import com.rocket.summer.framework.data.repository.query.RepositoryQuery;
import com.rocket.summer.framework.data.repository.query.parser.AbstractQueryCreator;

/**
 * {@link RepositoryFactorySupport} specific of handing Redis
 * {@link com.rocket.summer.framework.data.keyvalue.repository.KeyValueRepository}.
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 * @since 1.7
 */
public class RedisRepositoryFactory extends KeyValueRepositoryFactory {

    private final KeyValueOperations operations;

    /**
     * @param keyValueOperations
     * @see KeyValueRepositoryFactory#KeyValueRepositoryFactory(KeyValueOperations)
     */
    public RedisRepositoryFactory(KeyValueOperations keyValueOperations) {
        this(keyValueOperations, RedisQueryCreator.class);
    }

    /**
     * @param keyValueOperations
     * @param queryCreator
     * @see KeyValueRepositoryFactory#KeyValueRepositoryFactory(KeyValueOperations, Class)
     */
    public RedisRepositoryFactory(KeyValueOperations keyValueOperations,
                                  Class<? extends AbstractQueryCreator<?, ?>> queryCreator) {
        this(keyValueOperations, queryCreator, KeyValuePartTreeQuery.class);
    }

    /**
     * @param keyValueOperations
     * @param queryCreator
     * @param repositoryQueryType
     * @see KeyValueRepositoryFactory#KeyValueRepositoryFactory(KeyValueOperations, Class, Class)
     */
    public RedisRepositoryFactory(KeyValueOperations keyValueOperations,
                                  Class<? extends AbstractQueryCreator<?, ?>> queryCreator, Class<? extends RepositoryQuery> repositoryQueryType) {
        super(keyValueOperations, queryCreator, repositoryQueryType);

        this.operations = keyValueOperations;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.repository.support.KeyValueRepositoryFactory#getEntityInformation(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {

        RedisPersistentEntity<T> entity = (RedisPersistentEntity<T>) operations.getMappingContext()
                .getPersistentEntity(domainClass);
        EntityInformation<T, ID> entityInformation = (EntityInformation<T, ID>) new MappingRedisEntityInformation<T, ID>(
                entity);

        return entityInformation;
    }
}

