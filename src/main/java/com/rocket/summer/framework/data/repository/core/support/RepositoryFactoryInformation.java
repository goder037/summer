package com.rocket.summer.framework.data.repository.core.support;

import java.io.Serializable;
import java.util.List;

import com.rocket.summer.framework.data.mapping.PersistentEntity;
import com.rocket.summer.framework.data.mapping.context.MappingContext;
import com.rocket.summer.framework.data.repository.core.EntityInformation;
import com.rocket.summer.framework.data.repository.core.RepositoryInformation;
import com.rocket.summer.framework.data.repository.query.QueryMethod;

/**
 * Interface for components that can provide meta-information about a repository factory, the backing
 * {@link EntityInformation} and {@link RepositoryInformation} as well as the {@link QueryMethod}s exposed by the
 * repository.
 *
 * @author Oliver Gierke
 */
public interface RepositoryFactoryInformation<T, ID extends Serializable> {

    /**
     * Returns {@link EntityInformation} the repository factory is using.
     *
     * @return
     */
    EntityInformation<T, ID> getEntityInformation();

    /**
     * Returns the {@link RepositoryInformation} to determine meta-information about the repository being used.
     *
     * @return
     */
    RepositoryInformation getRepositoryInformation();

    /**
     * Returns the {@link PersistentEntity} managed by the underlying repository. Can be {@literal null} in case the
     * underlying persistence mechanism does not expose a {@link MappingContext}.
     *
     * @return
     */
    PersistentEntity<?, ?> getPersistentEntity();

    /**
     * Returns all {@link QueryMethod}s declared for that repository.
     *
     * @return
     */
    List<QueryMethod> getQueryMethods();
}

