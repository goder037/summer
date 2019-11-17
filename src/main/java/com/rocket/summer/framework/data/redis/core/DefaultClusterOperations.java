package com.rocket.summer.framework.data.redis.core;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.rocket.summer.framework.dao.DataAccessException;
import com.rocket.summer.framework.data.redis.connection.RedisClusterCommands.AddSlots;
import com.rocket.summer.framework.data.redis.connection.RedisClusterConnection;
import com.rocket.summer.framework.data.redis.connection.RedisClusterNode;
import com.rocket.summer.framework.data.redis.connection.RedisClusterNode.SlotRange;
import com.rocket.summer.framework.data.redis.connection.RedisServerCommands.MigrateOption;
import com.rocket.summer.framework.util.Assert;

/**
 * Default {@link ClusterOperations} implementation.
 *
 * @author Christoph Strobl
 * @since 1.7
 * @param <K>
 * @param <V>
 */
public class DefaultClusterOperations<K, V> extends AbstractOperations<K, V> implements ClusterOperations<K, V> {

    private final RedisTemplate<K, V> template;

    /**
     * Creates new {@link DefaultClusterOperations} delegating to the given {@link RedisTemplate}.
     *
     * @param template must not be {@literal null}.
     */
    public DefaultClusterOperations(RedisTemplate<K, V> template) {

        super(template);
        this.template = template;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.RedisClusterOperations#keys(com.rocket.summer.framework.data.redis.connection.RedisNode, byte[])
     */
    @Override
    public Set<K> keys(final RedisClusterNode node, final K pattern) {

        Assert.notNull(node, "ClusterNode must not be null.");

        return execute(new RedisClusterCallback<Set<K>>() {

            @Override
            public Set<K> doInRedis(RedisClusterConnection connection) throws DataAccessException {
                return deserializeKeys(connection.keys(node, rawKey(pattern)));
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.RedisClusterOperations#randomKey(com.rocket.summer.framework.data.redis.connection.RedisNode)
     */
    @Override
    public K randomKey(final RedisClusterNode node) {

        Assert.notNull(node, "ClusterNode must not be null.");

        return execute(new RedisClusterCallback<K>() {

            @Override
            public K doInRedis(RedisClusterConnection connection) throws DataAccessException {
                return deserializeKey(connection.randomKey(node));
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.RedisClusterOperations#ping(com.rocket.summer.framework.data.redis.connection.RedisNode)
     */
    @Override
    public String ping(final RedisClusterNode node) {

        Assert.notNull(node, "ClusterNode must not be null.");

        return execute(new RedisClusterCallback<String>() {

            @Override
            public String doInRedis(RedisClusterConnection connection) throws DataAccessException {
                return connection.ping(node);
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.RedisClusterOperations#addSlots(com.rocket.summer.framework.data.redis.connection.RedisClusterNode, int[])
     */
    @Override
    public void addSlots(final RedisClusterNode node, final int... slots) {

        Assert.notNull(node, "ClusterNode must not be null.");

        execute(new RedisClusterCallback<Void>() {

            @Override
            public Void doInRedis(RedisClusterConnection connection) throws DataAccessException {
                connection.clusterAddSlots(node, slots);
                return null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.RedisClusterOperations#addSlots(com.rocket.summer.framework.data.redis.connection.RedisClusterNode, com.rocket.summer.framework.data.redis.connection.RedisClusterNode.SlotRange)
     */
    @Override
    public void addSlots(RedisClusterNode node, SlotRange range) {

        Assert.notNull(node, "ClusterNode must not be null.");
        Assert.notNull(range, "Range must not be null.");

        addSlots(node, range.getSlotsArray());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.RedisClusterOperations#bgReWriteAof(com.rocket.summer.framework.data.redis.connection.RedisClusterNode)
     */
    @Override
    public void bgReWriteAof(final RedisClusterNode node) {

        Assert.notNull(node, "ClusterNode must not be null.");

        execute(new RedisClusterCallback<Void>() {

            @Override
            public Void doInRedis(RedisClusterConnection connection) throws DataAccessException {
                connection.bgReWriteAof(node);
                return null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.RedisClusterOperations#bgSave(com.rocket.summer.framework.data.redis.connection.RedisClusterNode)
     */
    @Override
    public void bgSave(final RedisClusterNode node) {

        Assert.notNull(node, "ClusterNode must not be null.");

        execute(new RedisClusterCallback<Void>() {

            @Override
            public Void doInRedis(RedisClusterConnection connection) throws DataAccessException {
                connection.bgSave(node);
                return null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.RedisClusterOperations#meet(com.rocket.summer.framework.data.redis.connection.RedisClusterNode)
     */
    @Override
    public void meet(final RedisClusterNode node) {

        Assert.notNull(node, "ClusterNode must not be null.");

        execute(new RedisClusterCallback<Void>() {

            @Override
            public Void doInRedis(RedisClusterConnection connection) throws DataAccessException {
                connection.clusterMeet(node);
                return null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.RedisClusterOperations#forget(com.rocket.summer.framework.data.redis.connection.RedisClusterNode)
     */
    @Override
    public void forget(final RedisClusterNode node) {

        Assert.notNull(node, "ClusterNode must not be null.");

        execute(new RedisClusterCallback<Void>() {

            @Override
            public Void doInRedis(RedisClusterConnection connection) throws DataAccessException {
                connection.clusterForget(node);
                return null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.RedisClusterOperations#flushDb(com.rocket.summer.framework.data.redis.connection.RedisClusterNode)
     */
    @Override
    public void flushDb(final RedisClusterNode node) {

        Assert.notNull(node, "ClusterNode must not be null.");

        execute(new RedisClusterCallback<Void>() {

            @Override
            public Void doInRedis(RedisClusterConnection connection) throws DataAccessException {
                connection.flushDb(node);
                return null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.RedisClusterOperations#getSlaves(com.rocket.summer.framework.data.redis.connection.RedisClusterNode)
     */
    @Override
    public Collection<RedisClusterNode> getSlaves(final RedisClusterNode node) {

        Assert.notNull(node, "ClusterNode must not be null.");

        return execute(new RedisClusterCallback<Collection<RedisClusterNode>>() {

            @Override
            public Collection<RedisClusterNode> doInRedis(RedisClusterConnection connection) throws DataAccessException {
                return connection.clusterGetSlaves(node);
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.RedisClusterOperations#save(com.rocket.summer.framework.data.redis.connection.RedisClusterNode)
     */
    @Override
    public void save(final RedisClusterNode node) {

        Assert.notNull(node, "ClusterNode must not be null.");

        execute(new RedisClusterCallback<Void>() {

            @Override
            public Void doInRedis(RedisClusterConnection connection) throws DataAccessException {
                connection.save(node);
                return null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.RedisClusterOperations#shutdown(com.rocket.summer.framework.data.redis.connection.RedisClusterNode)
     */
    @Override
    public void shutdown(final RedisClusterNode node) {

        Assert.notNull(node, "ClusterNode must not be null.");

        execute(new RedisClusterCallback<Void>() {

            @Override
            public Void doInRedis(RedisClusterConnection connection) throws DataAccessException {
                connection.shutdown(node);
                return null;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.ClusterOperations#reshard(com.rocket.summer.framework.data.redis.connection.RedisClusterNode, int, com.rocket.summer.framework.data.redis.connection.RedisClusterNode)
     */
    @Override
    public void reshard(final RedisClusterNode source, final int slot, final RedisClusterNode target) {

        Assert.notNull(source, "Source node must not be null.");
        Assert.notNull(target, "Target node must not be null.");

        execute(new RedisClusterCallback<Void>() {

            @Override
            public Void doInRedis(RedisClusterConnection connection) throws DataAccessException {

                connection.clusterSetSlot(target, slot, AddSlots.IMPORTING);
                connection.clusterSetSlot(source, slot, AddSlots.MIGRATING);
                List<byte[]> keys = connection.clusterGetKeysInSlot(slot, Integer.MAX_VALUE);

                for (byte[] key : keys) {
                    connection.migrate(key, source, 0, MigrateOption.COPY);
                }
                connection.clusterSetSlot(target, slot, AddSlots.NODE);
                return null;
            }
        });
    }

    /**
     * Executed wrapped command upon {@link RedisClusterConnection}.
     *
     * @param callback
     * @return
     */
    public <T> T execute(RedisClusterCallback<T> callback) {

        Assert.notNull(callback, "ClusterCallback must not be null!");

        RedisClusterConnection connection = template.getConnectionFactory().getClusterConnection();

        try {
            return callback.doInRedis(connection);
        } finally {
            connection.close();
        }
    }
}
