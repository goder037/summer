package com.rocket.summer.framework.data.repository.core;

/**
 * Abstraction of a map of {@link NamedQueries} that can be looked up by their names.
 *
 * @author Oliver Gierke
 */
public interface NamedQueries {

    /**
     * Returns whether the map contains a named query for the given name. If this method returns {@literal true} you can
     * expect {@link #getQuery(String)} to return a non-{@literal null} query for the very same name.
     *
     * @param queryName
     * @return
     */
    boolean hasQuery(String queryName);

    /**
     * Returns the named query with the given name or {@literal null} if none exists.
     *
     * @param queryName
     * @return
     */
    String getQuery(String queryName);
}

