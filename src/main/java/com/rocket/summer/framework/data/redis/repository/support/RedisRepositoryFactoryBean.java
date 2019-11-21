package com.rocket.summer.framework.data.redis.repository.support;

import java.io.Serializable;

import com.rocket.summer.framework.beans.factory.FactoryBean;
import com.rocket.summer.framework.data.keyvalue.core.KeyValueOperations;
import com.rocket.summer.framework.data.keyvalue.repository.support.KeyValueRepositoryFactoryBean;
import com.rocket.summer.framework.data.repository.Repository;
import com.rocket.summer.framework.data.repository.query.RepositoryQuery;
import com.rocket.summer.framework.data.repository.query.parser.AbstractQueryCreator;

/**
 * Adapter for Springs {@link FactoryBean} interface to allow easy setup of {@link RedisRepositoryFactory} via Spring
 * configuration.
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 * @param <T> The repository type.
 * @param <S> The repository domain type.
 * @param <ID> The repository id type.
 * @since 1.7
 */
public class RedisRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
        extends KeyValueRepositoryFactoryBean<T, S, ID> {

    /**
     * Creates a new {@link RedisRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public RedisRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.repository.support.KeyValueRepositoryFactoryBean#createRepositoryFactory(com.rocket.summer.framework.data.keyvalue.core.KeyValueOperations, java.lang.Class, java.lang.Class)
     */
    @Override
    protected RedisRepositoryFactory createRepositoryFactory(KeyValueOperations operations,
                                                             Class<? extends AbstractQueryCreator<?, ?>> queryCreator, Class<? extends RepositoryQuery> repositoryQueryType) {
        return new RedisRepositoryFactory(operations, queryCreator, repositoryQueryType);
    }
}

