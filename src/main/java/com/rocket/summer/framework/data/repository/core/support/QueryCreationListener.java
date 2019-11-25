package com.rocket.summer.framework.data.repository.core.support;

import com.rocket.summer.framework.data.repository.query.RepositoryQuery;

/**
 * Callback for listeners that want to execute functionality on {@link RepositoryQuery} creation.
 *
 * @author Oliver Gierke
 */
public interface QueryCreationListener<T extends RepositoryQuery> {

    /**
     * Will be invoked just after the {@link RepositoryQuery} was created.
     *
     * @param query
     */
    void onCreation(T query);
}

