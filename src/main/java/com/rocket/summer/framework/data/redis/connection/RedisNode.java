package com.rocket.summer.framework.data.redis.connection;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ObjectUtils;

/**
 * @author Christoph Strobl
 * @author Thomas Darimont
 * @author Mark Paluch
 * @since 1.4
 */
public class RedisNode implements NamedNode {

    String id;
    String name;
    String host;
    int port;
    NodeType type;
    String masterId;

    /**
     * Creates a new {@link RedisNode} with the given {@code host}, {@code port}.
     *
     * @param host must not be {@literal null}
     * @param port
     */
    public RedisNode(String host, int port) {

        Assert.notNull(host, "host must not be null!");

        this.host = host;
        this.port = port;
    }

    protected RedisNode() {}

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String asString() {
        return host + ":" + port;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     * @since 1.7
     */
    public String getMasterId() {
        return masterId;
    }

    /**
     * @return
     * @since 1.7
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * @since 1.7
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return
     * @since 1.7
     */
    public NodeType getType() {
        return type;
    }

    /**
     * @return
     * @since 1.7
     */
    public boolean isMaster() {
        return ObjectUtils.nullSafeEquals(NodeType.MASTER, getType());
    }

    /**
     * @return
     * @since 1.7
     */
    public boolean isSlave() {
        return ObjectUtils.nullSafeEquals(NodeType.SLAVE, getType());
    }

    /**
     * Get {@link RedisNodeBuilder} for creating new {@link RedisNode}.
     *
     * @return never {@literal null}.
     * @since 1.7
     */
    public static RedisNodeBuilder newRedisNode() {
        return new RedisNodeBuilder();
    }

    @Override
    public String toString() {
        return asString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ObjectUtils.nullSafeHashCode(host);
        result = prime * result + ObjectUtils.nullSafeHashCode(port);
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof RedisNode)) {
            return false;
        }

        RedisNode other = (RedisNode) obj;

        if (!ObjectUtils.nullSafeEquals(this.host, other.host)) {
            return false;
        }

        if (!ObjectUtils.nullSafeEquals(this.port, other.port)) {
            return false;
        }

        if (!ObjectUtils.nullSafeEquals(this.name, other.name)) {
            return false;
        }

        return true;
    }

    /**
     * @author Christoph Strobl
     * @since 1.7
     */
    public enum NodeType {
        MASTER, SLAVE
    }

    /**
     * Builder for creating new {@link RedisNode}.
     *
     * @author Christoph Strobl
     * @since 1.4
     */
    public static class RedisNodeBuilder {

        private RedisNode node;

        public RedisNodeBuilder() {
            node = new RedisNode();
        }

        /**
         * Define node name.
         */
        public RedisNodeBuilder withName(String name) {
            node.name = name;
            return this;
        }

        /**
         * Set host and port of server.
         *
         * @param host must not be {@literal null}.
         * @param port
         * @return
         */
        public RedisNodeBuilder listeningAt(String host, int port) {

            Assert.hasText(host, "Hostname must not be empty or null.");
            node.host = host;
            node.port = port;
            return this;
        }

        /**
         * Set id of server.
         *
         * @param id
         * @return
         */
        public RedisNodeBuilder withId(String id) {

            node.id = id;
            return this;
        }

        /**
         * Set server role.
         *
         * @param nodeType
         * @return
         * @since 1.7
         */
        public RedisNodeBuilder promotedAs(NodeType type) {

            node.type = type;
            return this;
        }

        /**
         * Set the id of the master node.
         *
         * @param masterId
         * @return
         * @since 1.7
         */
        public RedisNodeBuilder slaveOf(String masterId) {

            node.masterId = masterId;
            return this;
        }

        /**
         * Get the {@link RedisNode}.
         *
         * @return
         */
        public RedisNode build() {
            return this.node;
        }
    }

}

