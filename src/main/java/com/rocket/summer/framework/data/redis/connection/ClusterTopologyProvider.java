package com.rocket.summer.framework.data.redis.connection;

/**
 * {@link ClusterTopologyProvider} manages the current cluster topology and makes sure to refresh cluster information.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public interface ClusterTopologyProvider {

    /**
     * Get the current known {@link ClusterTopology}.
     *
     * @return never {@null}.
     */
    ClusterTopology getTopology();

}

