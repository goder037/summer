package com.rocket.summer.framework.data.redis.core;

import java.util.List;
import java.util.Map;

import com.rocket.summer.framework.data.geo.Circle;
import com.rocket.summer.framework.data.geo.Distance;
import com.rocket.summer.framework.data.geo.GeoResults;
import com.rocket.summer.framework.data.geo.Metric;
import com.rocket.summer.framework.data.geo.Point;
import com.rocket.summer.framework.data.redis.connection.DataType;
import com.rocket.summer.framework.data.redis.connection.RedisGeoCommands.GeoLocation;
import com.rocket.summer.framework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;

/**
 * Default implementation of {@link BoundGeoOperations}.
 *
 * @author Ninad Divadkar
 * @author Christoph Strobl
 * @since 1.8
 */
class DefaultBoundGeoOperations<K, M> extends DefaultBoundKeyOperations<K> implements BoundGeoOperations<K, M> {

    private final GeoOperations<K, M> ops;

    /**
     * Constructs a new {@code DefaultBoundGeoOperations}.
     *
     * @param key must not be {@literal null}.
     * @param operations must not be {@literal null}.
     */
    public DefaultBoundGeoOperations(K key, RedisOperations<K, M> operations) {

        super(key, operations);
        this.ops = operations.opsForGeo();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundGeoOperations#geoAdd(com.rocket.summer.framework.data.geo.Point, java.lang.Object)
     */
    @Override
    public Long geoAdd(Point point, M member) {
        return ops.geoAdd(getKey(), point, member);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundGeoOperations#geoAdd(com.rocket.summer.framework.data.redis.connection.RedisGeoCommands.GeoLocation)
     */
    @Override
    public Long geoAdd(GeoLocation<M> location) {
        return ops.geoAdd(getKey(), location);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundGeoOperations#geoAdd(java.util.Map)
     */
    @Override
    public Long geoAdd(Map<M, Point> memberCoordinateMap) {
        return ops.geoAdd(getKey(), memberCoordinateMap);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundGeoOperations#geoAdd(java.lang.Iterable)
     */
    @Override
    public Long geoAdd(Iterable<GeoLocation<M>> locations) {
        return ops.geoAdd(getKey(), locations);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundGeoOperations#geoDist(java.lang.Object, java.lang.Object)
     */
    @Override
    public Distance geoDist(M member1, M member2) {
        return ops.geoDist(getKey(), member1, member2);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundGeoOperations#geoDist(java.lang.Object, java.lang.Object, com.rocket.summer.framework.data.geo.Metric)
     */
    @Override
    public Distance geoDist(M member1, M member2, Metric unit) {
        return ops.geoDist(getKey(), member1, member2, unit);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundGeoOperations#geoHash(java.lang.Object[])
     */
    @Override
    public List<String> geoHash(M... members) {
        return ops.geoHash(getKey(), members);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundGeoOperations#geoPos(java.lang.Object[])
     */
    @Override
    public List<Point> geoPos(M... members) {
        return ops.geoPos(getKey(), members);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundGeoOperations#geoRadius(com.rocket.summer.framework.data.geo.Circle)
     */
    @Override
    public GeoResults<GeoLocation<M>> geoRadius(Circle within) {
        return ops.geoRadius(getKey(), within);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundGeoOperations#geoRadius(com.rocket.summer.framework.data.geo.Circle, com.rocket.summer.framework.data.redis.core.GeoRadiusCommandArgs)
     */
    @Override
    public GeoResults<GeoLocation<M>> geoRadius(Circle within, GeoRadiusCommandArgs param) {
        return ops.geoRadius(getKey(), within, param);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundGeoOperations#geoRadiusByMember(java.lang.Object, java.lang.Object, double)
     */
    @Override
    public GeoResults<GeoLocation<M>> geoRadiusByMember(K key, M member, double radius) {
        return ops.geoRadiusByMember(getKey(), member, radius);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundGeoOperations#geoRadiusByMember(java.lang.Object, com.rocket.summer.framework.data.geo.Distance)
     */
    @Override
    public GeoResults<GeoLocation<M>> geoRadiusByMember(M member, Distance distance) {
        return ops.geoRadiusByMember(getKey(), member, distance);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundGeoOperations#geoRadiusByMember(java.lang.Object, com.rocket.summer.framework.data.geo.Distance, com.rocket.summer.framework.data.redis.core.GeoRadiusCommandArgs)
     */
    @Override
    public GeoResults<GeoLocation<M>> geoRadiusByMember(M member, Distance distance, GeoRadiusCommandArgs param) {
        return ops.geoRadiusByMember(getKey(), member, distance, param);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundGeoOperations#geoRemove(java.lang.Object[])
     */
    @Override
    public Long geoRemove(M... members) {
        return ops.geoRemove(getKey(), members);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.BoundKeyOperations#getType()
     */
    @Override
    public DataType getType() {
        return DataType.ZSET;
    }

}

