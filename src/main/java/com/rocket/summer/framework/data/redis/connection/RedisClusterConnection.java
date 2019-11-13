package com.rocket.summer.framework.data.redis.connection;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.rocket.summer.framework.data.redis.core.types.RedisClientInfo;

/**
 * {@link RedisClusterConnection} allows sending commands to dedicated nodes within the cluster. A
 * {@link RedisClusterNode} can be obtained from {@link #clusterGetNodes()} or it can be constructed using either
 * {@link RedisClusterNode#getHost() host} and {@link RedisClusterNode#getPort()} or the {@link RedisClusterNode#getId()
 * node Id}.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 1.7
 */
public interface RedisClusterConnection extends RedisConnection, RedisClusterCommands {

    /**
     * @param node must not be {@literal null}.
     * @return
     * @see RedisConnectionCommands#ping()
     */
    String ping(RedisClusterNode node);

    /**
     * @param node must not be {@literal null}.
     * @see RedisServerCommands#bgReWriteAof()
     */
    void bgReWriteAof(RedisClusterNode node);

    /**
     * @param node must not be {@literal null}.
     * @see RedisServerCommands#bgSave()
     */
    void bgSave(RedisClusterNode node);

    /**
     * @param node must not be {@literal null}.
     * @return
     * @see RedisServerCommands#lastSave()
     */
    Long lastSave(RedisClusterNode node);

    /**
     * @param node must not be {@literal null}.
     * @see RedisServerCommands#save()
     */
    void save(RedisClusterNode node);

    /**
     * @param node must not be {@literal null}.
     * @return
     * @see RedisServerCommands#dbSize()
     */
    Long dbSize(RedisClusterNode node);

    /**
     * @param node must not be {@literal null}.
     * @see RedisServerCommands#flushDb()
     */
    void flushDb(RedisClusterNode node);

    /**
     * @param node must not be {@literal null}.
     * @see RedisServerCommands#flushAll()
     */
    void flushAll(RedisClusterNode node);

    /**
     * @param node must not be {@literal null}.
     * @return
     * @see RedisServerCommands#info()
     */
    Properties info(RedisClusterNode node);

    /**
     * @param node must not be {@literal null}.
     * @param section
     * @return
     * @see RedisServerCommands#info(String)
     */
    Properties info(RedisClusterNode node, String section);

    /**
     * @param node must not be {@literal null}.
     * @param pattern must not be {@literal null}.
     * @return
     * @see RedisKeyCommands#keys(byte[])
     */
    Set<byte[]> keys(RedisClusterNode node, byte[] pattern);

    /**
     * @param node must not be {@literal null}.
     * @return
     * @see RedisKeyCommands#randomKey()
     */
    byte[] randomKey(RedisClusterNode node);

    /**
     * @param node must not be {@literal null}.
     * @see RedisServerCommands#shutdown()
     */
    void shutdown(RedisClusterNode node);

    /**
     * @param node must not be {@literal null}.
     * @param pattern
     * @return
     * @see RedisServerCommands#getConfig(String)
     */
    List<String> getConfig(RedisClusterNode node, String pattern);

    /**
     * @param node must not be {@literal null}.
     * @param param
     * @param value
     * @see RedisServerCommands#setConfig(String, String)
     */
    void setConfig(RedisClusterNode node, String param, String value);

    /**
     * @param node must not be {@literal null}.
     * @see RedisServerCommands#resetConfigStats()
     */
    void resetConfigStats(RedisClusterNode node);

    /**
     * @param node must not be {@literal null}.
     * @return
     * @see RedisServerCommands#time()
     */
    Long time(RedisClusterNode node);

    /**
     * @param node must not be {@literal null}.
     * @return
     * @see RedisServerCommands#getClientList()
     */
    public List<RedisClientInfo> getClientList(RedisClusterNode node);

}

