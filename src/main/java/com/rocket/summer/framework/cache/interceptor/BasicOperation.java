package com.rocket.summer.framework.cache.interceptor;

import java.util.Set;

/**
 * The base interface that all cache operations must implement.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
public interface BasicOperation {

    /**
     * Return the cache name(s) associated with the operation.
     */
    Set<String> getCacheNames();

}
