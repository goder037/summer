package com.rocket.summer.framework.data.redis.connection;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.rocket.summer.framework.data.redis.ClusterStateFailureException;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;

/**
 * {@link ClusterTopology} holds snapshot like information about {@link RedisClusterNode}s.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 1.7
 */
public class ClusterTopology {

    private final Set<RedisClusterNode> nodes;

    /**
     * Creates new instance of {@link ClusterTopology}.
     *
     * @param nodes can be {@literal null}.
     */
    public ClusterTopology(Set<RedisClusterNode> nodes) {
        this.nodes = nodes != null ? nodes : Collections.<RedisClusterNode> emptySet();
    }

    /**
     * Get all {@link RedisClusterNode}s.
     *
     * @return never {@literal null}.
     */
    public Set<RedisClusterNode> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }

    /**
     * Get all nodes (master and slave) in cluster where {@code link-state} is {@literal connected} and {@code flags} does
     * not contain {@literal fail} or {@literal fail?}.
     *
     * @return never {@literal null}.
     */
    public Set<RedisClusterNode> getActiveNodes() {

        Set<RedisClusterNode> activeNodes = new LinkedHashSet<RedisClusterNode>(nodes.size());
        for (RedisClusterNode node : nodes) {
            if (node.isConnected() && !node.isMarkedAsFail()) {
                activeNodes.add(node);
            }
        }
        return activeNodes;
    }

    /**
     * Get all master nodes in cluster where {@code link-state} is {@literal connected} and {@code flags} does not contain
     * {@literal fail} or {@literal fail?}.
     *
     * @return never {@literal null}.
     */
    public Set<RedisClusterNode> getActiveMasterNodes() {

        Set<RedisClusterNode> activeMasterNodes = new LinkedHashSet<RedisClusterNode>(nodes.size());
        for (RedisClusterNode node : nodes) {
            if (node.isMaster() && node.isConnected() && !node.isMarkedAsFail()) {
                activeMasterNodes.add(node);
            }
        }
        return activeMasterNodes;
    }

    /**
     * Get all master nodes in cluster.
     *
     * @return never {@literal null}.
     */
    public Set<RedisClusterNode> getMasterNodes() {

        Set<RedisClusterNode> masterNodes = new LinkedHashSet<RedisClusterNode>(nodes.size());
        for (RedisClusterNode node : nodes) {
            if (node.isMaster()) {
                masterNodes.add(node);
            }
        }
        return masterNodes;
    }

    /**
     * Get the {@link RedisClusterNode}s (master and slave) serving s specific slot.
     *
     * @param slot
     * @return never {@literal null}.
     */
    public Set<RedisClusterNode> getSlotServingNodes(int slot) {

        Set<RedisClusterNode> slotServingNodes = new LinkedHashSet<RedisClusterNode>(nodes.size());
        for (RedisClusterNode node : nodes) {
            if (node.servesSlot(slot)) {
                slotServingNodes.add(node);
            }
        }
        return slotServingNodes;
    }

    /**
     * Get the {@link RedisClusterNode} that is the current master serving the given key.
     *
     * @param key must not be {@literal null}.
     * @return
     * @throws ClusterStateFailureException
     */
    public RedisClusterNode getKeyServingMasterNode(byte[] key) {

        Assert.notNull(key, "Key for node lookup must not be null!");

        int slot = ClusterSlotHashUtil.calculateSlot(key);
        for (RedisClusterNode node : nodes) {
            if (node.isMaster() && node.servesSlot(slot)) {
                return node;
            }
        }
        throw new ClusterStateFailureException(String.format("Could not find master node serving slot %s for key '%s',",
                slot, key));
    }

    /**
     * Get the {@link RedisClusterNode} matching given {@literal host} and {@literal port}.
     *
     * @param host must not be {@literal null}.
     * @param port
     * @return
     * @throws ClusterStateFailureException
     */
    public RedisClusterNode lookup(String host, int port) {

        for (RedisClusterNode node : nodes) {
            if (host.equals(node.getHost()) && port == node.getPort()) {
                return node;
            }
        }
        throw new ClusterStateFailureException(String.format(
                "Could not find node at %s:%s. Is your cluster info up to date?", host, port));
    }

    /**
     * Get the {@link RedisClusterNode} matching given {@literal nodeId}.
     *
     * @param nodeId must not be {@literal null}.
     * @return
     * @throws ClusterStateFailureException
     */
    public RedisClusterNode lookup(String nodeId) {

        Assert.notNull(nodeId, "NodeId must not be null");

        for (RedisClusterNode node : nodes) {
            if (nodeId.equals(node.getId())) {
                return node;
            }
        }
        throw new ClusterStateFailureException(String.format(
                "Could not find node at %s. Is your cluster info up to date?", nodeId));
    }

    /**
     * Get the {@link RedisClusterNode} matching matching either {@link RedisClusterNode#getHost() host} and {@link RedisClusterNode#getPort() port}
     * or {@link RedisClusterNode#getId() nodeId}
     *
     * @param node must not be {@literal null}
     * @return
     * @throws ClusterStateFailureException
     */
    public RedisClusterNode lookup(RedisClusterNode node) {

        Assert.notNull(node, "RedisClusterNode must not be null");

        if(nodes.contains(node) && StringUtils.hasText(node.getHost()) && StringUtils.hasText(node.getId())){
            return node;
        }

        if (StringUtils.hasText(node.getHost())) {
            return lookup(node.getHost(), node.getPort());
        }

        if (StringUtils.hasText(node.getId())) {
            return lookup(node.getId());
        }

        throw new ClusterStateFailureException(
                String.format("Could not find node at %s. Have you provided either host and port or the nodeId?", node));
    }

    /**
     * @param key must not be {@literal null}.
     * @return {@literal null}.
     */
    public Set<RedisClusterNode> getKeyServingNodes(byte[] key) {

        Assert.notNull(key, "Key must not be null for Cluster Node lookup.");
        return getSlotServingNodes(ClusterSlotHashUtil.calculateSlot(key));
    }
}

