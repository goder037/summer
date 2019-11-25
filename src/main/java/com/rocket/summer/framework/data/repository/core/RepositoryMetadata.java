package com.rocket.summer.framework.data.repository.core;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

import com.rocket.summer.framework.data.repository.support.Repositories;

/**
 * Metadata for repository interfaces.
 *
 * @author Oliver Gierke
 */
public interface RepositoryMetadata {

    /**
     * Returns the id class the given class is declared for.
     *
     * @return the id class of the entity managed by the repository for or {@code null} if none found.
     */
    Class<? extends Serializable> getIdType();

    /**
     * Returns the domain class the repository is declared for.
     *
     * @return the domain class the repository is handling or {@code null} if none found.
     */
    Class<?> getDomainType();

    /**
     * Returns the repository interface.
     *
     * @return
     */
    Class<?> getRepositoryInterface();

    /**
     * Returns the domain class returned by the given {@link Method}. Will extract the type from {@link Collection}s and
     * {@link com.rocket.summer.framework.data.domain.Page} as well.
     *
     * @param method
     * @return
     */
    Class<?> getReturnedDomainClass(Method method);

    /**
     * Returns {@link CrudMethods} meta information for the repository.
     *
     * @return
     */
    CrudMethods getCrudMethods();

    /**
     * Returns whether the repository is a paging one.
     *
     * @return
     */
    boolean isPagingRepository();

    /**
     * Returns the set of types the repository shall be discoverable for when trying to look up a repository by domain
     * type.
     *
     * @see Repositories#getRepositoryFor(Class)
     * @return the set of types the repository shall be discoverable for when trying to look up a repository by domain
     *         type, must not be {@literal null}.
     * @since 1.11
     */
    Set<Class<?>> getAlternativeDomainTypes();
}

