package com.rocket.summer.framework.data.redis.connection;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.CollectionUtils;

/**
 * Representation of a Redis server within the cluster.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 1.7
 */
public class RedisClusterNode extends RedisNode {

    private SlotRange slotRange;
    private LinkState linkState;
    private Set<Flag> flags;

    protected RedisClusterNode() {
        super();
    }

    /**
     * Creates new {@link RedisClusterNode} with empty {@link SlotRange}.
     *
     * @param host must not be {@literal null}.
     * @param port
     */
    public RedisClusterNode(String host, int port) {
        this(host, port, new SlotRange(Collections.<Integer> emptySet()));
    }

    /**
     * Creates new {@link RedisClusterNode} with an id and empty {@link SlotRange}.
     *
     * @param id must not be {@literal null}.
     */
    public RedisClusterNode(String id) {

        this(new SlotRange(Collections.<Integer> emptySet()));
        Assert.notNull(id, "Id must not be null");
        this.id = id;
    }

    /**
     * Creates new {@link RedisClusterNode} with given {@link SlotRange}.
     *
     * @param host must not be {@literal null}.
     * @param port
     * @param slotRange can be {@literal null}.
     */
    public RedisClusterNode(String host, int port, SlotRange slotRange) {

        super(host, port);
        this.slotRange = slotRange != null ? slotRange : new SlotRange(Collections.<Integer> emptySet());
    }

    /**
     * Creates new {@link RedisClusterNode} with given {@link SlotRange}.
     *
     * @param slotRange can be {@literal null}.
     */
    public RedisClusterNode(SlotRange slotRange) {

        super();
        this.slotRange = slotRange != null ? slotRange : new SlotRange(Collections.<Integer> emptySet());
    }

    /**
     * Get the served {@link SlotRange}.
     *
     * @return never {@literal null}.
     */
    public SlotRange getSlotRange() {
        return slotRange;
    }

    /**
     * @param slot
     * @return true if slot is covered.
     */
    public boolean servesSlot(int slot) {
        return slotRange.contains(slot);
    }

    /**
     * @return
     */
    public LinkState getLinkState() {
        return linkState;
    }

    /**
     * @return true if node is connected to cluster.
     */
    public boolean isConnected() {
        return LinkState.CONNECTED.equals(linkState);
    }

    /**
     * @return never {@literal null}.
     */
    public Set<Flag> getFlags() {
        return flags == null ? Collections.<Flag> emptySet() : flags;
    }

    /**
     * @return true if node is marked as failing.
     */
    public boolean isMarkedAsFail() {

        if (!CollectionUtils.isEmpty(flags)) {
            return flags.contains(Flag.FAIL) || flags.contains(Flag.PFAIL);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisNode#toString()
     */
    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Get {@link RedisClusterNodeBuilder} for creating new {@link RedisClusterNode}.
     *
     * @return never {@literal null}.
     */
    public static RedisClusterNodeBuilder newRedisClusterNode() {
        return new RedisClusterNodeBuilder();
    }

    /**
     * @author Christoph Strobl
     * @since 1.7
     */
    public static class SlotRange {

        private final Set<Integer> range;

        /**
         * @param lowerBound must not be {@literal null}.
         * @param upperBound must not be {@literal null}.
         */
        public SlotRange(Integer lowerBound, Integer upperBound) {

            Assert.notNull(lowerBound, "LowerBound must not be null!");
            Assert.notNull(upperBound, "UpperBound must not be null!");

            this.range = new LinkedHashSet<Integer>();
            for (int i = lowerBound; i <= upperBound; i++) {
                this.range.add(i);
            }
        }

        public SlotRange(Collection<Integer> range) {
            this.range = CollectionUtils.isEmpty(range) ? Collections.<Integer> emptySet()
                    : new LinkedHashSet<Integer>(range);
        }

        @Override
        public String toString() {
            return range.toString();
        }

        /**
         * @param slot
         * @return true when slot is part of the range.
         */
        public boolean contains(int slot) {
            return range.contains(slot);
        }

        /**
         * @return
         */
        public Set<Integer> getSlots() {
            return Collections.unmodifiableSet(range);
        }

        public int[] getSlotsArray() {

            int[] slots = new int[range.size()];
            int pos = 0;

            for (Integer value : range) {
                slots[pos++] = value.intValue();
            }

            return slots;
        }
    }

    /**
     * @author Christoph Strobl
     * @since 1.7
     */
    public static enum LinkState {
        CONNECTED, DISCONNECTED
    }

    /**
     * @author Christoph Strobl
     * @since 1.7
     */
    public static enum Flag {

        MYSELF("myself"), MASTER("master"), SLAVE("slave"), FAIL("fail"), PFAIL("fail?"), HANDSHAKE("handshake"), NOADDR(
                "noaddr"), NOFLAGS("noflags");

        private String raw;

        Flag(String raw) {
            this.raw = raw;
        }

        public String getRaw() {
            return raw;
        }

    }

    /**
     * Builder for creating new {@link RedisClusterNode}.
     *
     * @author Christoph Strobl
     * @since 1.7
     */
    public static class RedisClusterNodeBuilder extends RedisNodeBuilder {

        Set<Flag> flags;
        LinkState linkState;
        SlotRange slotRange;

        public RedisClusterNodeBuilder() {

        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.redis.connection.RedisNode.RedisNodeBuilder#listeningAt(java.lang.String, int)
         */
        @Override
        public RedisClusterNodeBuilder listeningAt(String host, int port) {
            super.listeningAt(host, port);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.redis.connection.RedisNode.RedisNodeBuilder#withName(java.lang.String)
         */
        @Override
        public RedisClusterNodeBuilder withName(String name) {
            super.withName(name);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.redis.connection.RedisNode.RedisNodeBuilder#withId(java.lang.String)
         */
        @Override
        public RedisClusterNodeBuilder withId(String id) {
            super.withId(id);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.redis.connection.RedisNode.RedisNodeBuilder#promotedAs(com.rocket.summer.framework.data.redis.connection.RedisNode.NodeType)
         */
        @Override
        public RedisClusterNodeBuilder promotedAs(NodeType nodeType) {
            super.promotedAs(nodeType);
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.redis.connection.RedisNode.RedisNodeBuilder#slaveOf(java.lang.String)
         */
        public RedisClusterNodeBuilder slaveOf(String masterId) {
            super.slaveOf(masterId);
            return this;
        }

        /**
         * Set flags for node.
         *
         * @param flags
         * @return
         */
        public RedisClusterNodeBuilder withFlags(Set<Flag> flags) {

            this.flags = flags;
            return this;
        }

        /**
         * Set {@link SlotRange}.
         *
         * @param range
         * @return
         */
        public RedisClusterNodeBuilder serving(SlotRange range) {

            this.slotRange = range;
            return this;
        }

        /**
         * Set {@link LinkState}.
         *
         * @param linkState
         * @return
         */
        public RedisClusterNodeBuilder linkState(LinkState linkState) {
            this.linkState = linkState;
            return this;
        }

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.redis.connection.RedisNode.RedisNodeBuilder#build()
         */
        @Override
        public RedisClusterNode build() {

            RedisNode base = super.build();

            RedisClusterNode node;
            if (base.host != null) {
                node = new RedisClusterNode(base.getHost(), base.getPort(), slotRange);
            } else {
                node = new RedisClusterNode(slotRange);
            }
            node.id = base.id;
            node.type = base.type;
            node.masterId = base.masterId;
            node.name = base.name;
            node.flags = flags;
            node.linkState = linkState;
            return node;
        }
    }

}

