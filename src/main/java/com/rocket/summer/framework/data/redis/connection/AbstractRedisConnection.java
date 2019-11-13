package com.rocket.summer.framework.data.redis.connection;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.rocket.summer.framework.dao.DataAccessException;
import com.rocket.summer.framework.dao.InvalidDataAccessApiUsageException;
import com.rocket.summer.framework.dao.InvalidDataAccessResourceUsageException;
import com.rocket.summer.framework.data.redis.RedisSystemException;

/**
 * @author Christoph Strobl
 * @since 1.4
 */
public abstract class AbstractRedisConnection implements RedisConnection {

    private RedisSentinelConfiguration sentinelConfiguration;
    private ConcurrentHashMap<RedisNode, RedisSentinelConnection> connectionCache = new ConcurrentHashMap<RedisNode, RedisSentinelConnection>();

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisConnection#getSentinelCommands()
     */
    @Override
    public RedisSentinelConnection getSentinelConnection() {

        if (!hasRedisSentinelConfigured()) {
            throw new InvalidDataAccessResourceUsageException("No sentinels configured.");
        }

        RedisNode node = selectActiveSentinel();
        RedisSentinelConnection connection = connectionCache.get(node);
        if (connection == null || !connection.isOpen()) {
            connection = getSentinelConnection(node);
            connectionCache.putIfAbsent(node, connection);
        }
        return connection;
    }

    public void setSentinelConfiguration(RedisSentinelConfiguration sentinelConfiguration) {
        this.sentinelConfiguration = sentinelConfiguration;
    }

    public boolean hasRedisSentinelConfigured() {
        return this.sentinelConfiguration != null;
    }

    private RedisNode selectActiveSentinel() {

        for (RedisNode node : this.sentinelConfiguration.getSentinels()) {
            if (isActive(node)) {
                return node;
            }
        }

        throw new InvalidDataAccessApiUsageException("Could not find any active sentinels");
    }

    /**
     * Check if node is active by sending ping.
     *
     * @param node
     * @return
     */
    protected boolean isActive(RedisNode node) {
        return false;
    }

    /**
     * Get {@link RedisSentinelCommands} connected to given node.
     *
     * @param sentinel
     * @return
     */
    protected RedisSentinelConnection getSentinelConnection(RedisNode sentinel) {
        throw new UnsupportedOperationException("Sentinel is not supported by this client.");
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisConnection#close()
     */
    @Override
    public void close() throws DataAccessException {

        if (!connectionCache.isEmpty()) {
            for (RedisNode node : connectionCache.keySet()) {
                RedisSentinelConnection connection = connectionCache.remove(node);
                if (connection.isOpen()) {
                    try {
                        connection.close();
                    } catch (IOException e) {
                        throw new RedisSystemException("Failed to close sentinel connection", e);
                    }
                }
            }
        }
    }

}

