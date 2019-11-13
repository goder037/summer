package com.rocket.summer.framework.data.redis.connection;

/**
 * {@link ClusterNodeResourceProvider} provides access to low level client api to directly execute operations against a
 * Redis instance.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public interface ClusterNodeResourceProvider {

    /**
     * Get the client resource for the given node.
     *
     * @param node must not be {@literal null}.
     * @return
     */
    <S> S getResourceForSpecificNode(RedisClusterNode node);

    /**
     * Return the resource object for the given node. This can mean free up resources or return elements back to a pool.
     *
     * @param node must not be {@literal null}.
     * @param resource must not be {@literal null}.
     */
    void returnResourceForSpecificNode(RedisClusterNode node, Object resource);

}

