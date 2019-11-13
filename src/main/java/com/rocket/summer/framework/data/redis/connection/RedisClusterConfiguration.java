package com.rocket.summer.framework.data.redis.connection;

import static com.rocket.summer.framework.util.Assert.*;
import static com.rocket.summer.framework.util.StringUtils.*;

import java.util.*;

import com.rocket.summer.framework.core.env.MapPropertySource;
import com.rocket.summer.framework.core.env.PropertySource;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.NumberUtils;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Configuration class used for setting up {@link RedisConnection} via {@link RedisConnectionFactory} using connecting
 * to <a href="http://redis.io/topics/cluster-spec">Redis Cluster</a>. Useful when setting up a high availability Redis
 * environment.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 1.7
 */
public class RedisClusterConfiguration {

    private static final String REDIS_CLUSTER_NODES_CONFIG_PROPERTY = "spring.redis.cluster.nodes";
    private static final String REDIS_CLUSTER_MAX_REDIRECTS_CONFIG_PROPERTY = "spring.redis.cluster.max-redirects";

    private Set<RedisNode> clusterNodes;
    private Integer maxRedirects;

    /**
     * Creates new {@link RedisClusterConfiguration}.
     */
    public RedisClusterConfiguration() {
        this(new MapPropertySource("RedisClusterConfiguration", Collections.<String, Object> emptyMap()));
    }

    /**
     * Creates {@link RedisClusterConfiguration} for given hostPort combinations.
     *
     * <pre>
     * clusterHostAndPorts[0] = 127.0.0.1:23679
     * clusterHostAndPorts[1] = 127.0.0.1:23680
     * ...
     *
     * <pre>
     *
     * @param clusterNodes must not be
     * {@literal null}.
     */
    public RedisClusterConfiguration(Collection<String> clusterNodes) {
        this(new MapPropertySource("RedisClusterConfiguration", asMap(clusterNodes, -1, -1, null)));
    }

    /**
     * Creates {@link RedisClusterConfiguration} looking up values in given {@link PropertySource}.
     *
     * <pre>
     * <code>
     * spring.redis.cluster.nodes=127.0.0.1:23679,127.0.0.1:23680,127.0.0.1:23681
     * spring.redis.cluster.timeout=5
     * spring.redis.cluster.max-redirects=3
     * spring.redis.cluster.password=foobar
     * </code>
     * </pre>
     *
     * @param propertySource must not be {@literal null}.
     */
    public RedisClusterConfiguration(PropertySource<?> propertySource) {

        notNull(propertySource, "PropertySource must not be null!");

        this.clusterNodes = new LinkedHashSet<RedisNode>();

        if (propertySource.containsProperty(REDIS_CLUSTER_NODES_CONFIG_PROPERTY)) {
            appendClusterNodes(commaDelimitedListToSet(propertySource.getProperty(REDIS_CLUSTER_NODES_CONFIG_PROPERTY)
                    .toString()));
        }
        if (propertySource.containsProperty(REDIS_CLUSTER_MAX_REDIRECTS_CONFIG_PROPERTY)) {
            this.maxRedirects = NumberUtils.parseNumber(
                    propertySource.getProperty(REDIS_CLUSTER_MAX_REDIRECTS_CONFIG_PROPERTY).toString(), Integer.class);
        }
    }

    /**
     * Set {@literal cluster nodes} to connect to.
     *
     * @param nodes must not be {@literal null}.
     */
    public void setClusterNodes(Iterable<RedisNode> nodes) {

        notNull(nodes, "Cannot set cluster nodes to 'null'.");

        this.clusterNodes.clear();

        for (RedisNode clusterNode : nodes) {
            addClusterNode(clusterNode);
        }
    }

    /**
     * Returns an {@link Collections#unmodifiableSet(Set)} of {@literal cluster nodes}.
     *
     * @return {@link Set} of nodes. Never {@literal null}.
     */
    public Set<RedisNode> getClusterNodes() {
        return Collections.unmodifiableSet(clusterNodes);
    }

    /**
     * Add a cluster node to configuration.
     *
     * @param node must not be {@literal null}.
     */
    public void addClusterNode(RedisNode node) {

        notNull(node, "ClusterNode must not be 'null'.");
        this.clusterNodes.add(node);
    }

    /**
     * @return
     */
    public RedisClusterConfiguration clusterNode(RedisNode node) {

        this.clusterNodes.add(node);
        return this;
    }

    /**
     * @return
     */
    public Integer getMaxRedirects() {
        return maxRedirects != null && maxRedirects > Integer.MIN_VALUE ? maxRedirects : null;
    }

    /**
     * @param maxRedirects
     */
    public void setMaxRedirects(int maxRedirects) {

        Assert.isTrue(maxRedirects >= 0, "MaxRedirects must be greater or equal to 0");
        this.maxRedirects = maxRedirects;
    }

    /**
     * @param host
     * @param port
     * @return
     */
    public RedisClusterConfiguration clusterNode(String host, Integer port) {
        return clusterNode(new RedisNode(host, port));
    }

    private void appendClusterNodes(Set<String> hostAndPorts) {

        for (String hostAndPort : hostAndPorts) {
            addClusterNode(readHostAndPortFromString(hostAndPort));
        }
    }

    private RedisNode readHostAndPortFromString(String hostAndPort) {

        String[] args = split(hostAndPort, ":");

        notNull(args, "HostAndPort need to be seperated by  ':'.");
        isTrue(args.length == 2, "Host and Port String needs to specified as host:port");
        return new RedisNode(args[0], Integer.valueOf(args[1]).intValue());
    }

    /**
     * @param clusterHostAndPorts must not be {@literal null} or empty.
     * @param timeout
     * @param redirects
     * @param password can be {@literal null} or empty.
     * @return
     */
    private static Map<String, Object> asMap(Collection<String> clusterHostAndPorts, long timeout, int redirects,
                                             String password) {

        notNull(clusterHostAndPorts, "ClusterHostAndPorts must not be null!");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(REDIS_CLUSTER_NODES_CONFIG_PROPERTY, StringUtils.collectionToCommaDelimitedString(clusterHostAndPorts));

        if (redirects >= 0) {
            map.put(REDIS_CLUSTER_MAX_REDIRECTS_CONFIG_PROPERTY, Integer.valueOf(redirects));
        }

        return map;
    }
}

