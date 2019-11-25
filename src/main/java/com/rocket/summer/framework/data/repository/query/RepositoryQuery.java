package com.rocket.summer.framework.data.repository.query;

/**
 * Interface for a query abstraction.
 *
 * @author Oliver Gierke
 */
public interface RepositoryQuery {

    /**
     * Executes the {@link RepositoryQuery} with the given parameters.
     *
     * @param parameters
     * @return
     */
    public Object execute(Object[] parameters);

    /**
     * Returns the
     *
     * @return
     */
    public QueryMethod getQueryMethod();
}
