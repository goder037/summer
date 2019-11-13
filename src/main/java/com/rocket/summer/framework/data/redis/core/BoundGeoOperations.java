package com.rocket.summer.framework.data.redis.core;

import java.util.List;
import java.util.Map;

import com.rocket.summer.framework.data.geo.Circle;
import com.rocket.summer.framework.data.geo.Distance;
import com.rocket.summer.framework.data.geo.GeoResults;
import com.rocket.summer.framework.data.geo.Metric;
import com.rocket.summer.framework.data.geo.Point;
import com.rocket.summer.framework.data.redis.connection.RedisGeoCommands.GeoLocation;
import com.rocket.summer.framework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;

/**
 * {@link GeoOperations} bound to a certain key.
 *
 * @author Ninad Divadkar
 * @author Christoph Strobl
 * @since 1.8
 */
public interface BoundGeoOperations<K, M> extends BoundKeyOperations<K> {

    /**
     * Add {@link Point} with given member {@literal name} to {@literal key}.
     *
     * @param point must not be {@literal null}.
     * @param member must not be {@literal null}.
     * @return Number of elements added.
     * @see <a href="http://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     */
    Long geoAdd(Point point, M member);

    /**
     * Add {@link GeoLocation} to {@literal key}.
     *
     * @param location must not be {@literal null}.
     * @return Number of elements added.
     * @see <a href="http://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     */
    Long geoAdd(GeoLocation<M> location);

    /**
     * Add {@link Map} of member / {@link Point} pairs to {@literal key}.
     *
     * @param memberCoordinateMap must not be {@literal null}.
     * @return Number of elements added.
     * @see <a href="http://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     */
    Long geoAdd(Map<M, Point> memberCoordinateMap);

    /**
     * Add {@link GeoLocation}s to {@literal key}
     *
     * @param locations must not be {@literal null}.
     * @return Number of elements added.
     * @see <a href="http://redis.io/commands/geoadd">Redis Documentation: GEOADD</a>
     */
    Long geoAdd(Iterable<GeoLocation<M>> locations);

    /**
     * Get the {@link Distance} between {@literal member1} and {@literal member2}.
     *
     * @param member1 must not be {@literal null}.
     * @param member2 must not be {@literal null}.
     * @return can be {@literal null}.
     * @see <a href="http://redis.io/commands/geodist">Redis Documentation: GEODIST</a>
     */
    Distance geoDist(M member1, M member2);

    /**
     * Get the {@link Distance} between {@literal member1} and {@literal member2} in the given {@link Metric}.
     *
     * @param member1 must not be {@literal null}.
     * @param member2 must not be {@literal null}.
     * @param metric must not be {@literal null}.
     * @return can be {@literal null}.
     * @see <a href="http://redis.io/commands/geodist">Redis Documentation: GEODIST</a>
     */
    Distance geoDist(M member1, M member2, Metric metric);

    /**
     * Get Geohash representation of the position for one or more {@literal member}s.
     *
     * @param members must not be {@literal null}.
     * @return never {@literal null}.
     * @see <a href="http://redis.io/commands/geohash">Redis Documentation: GEOHASH</a>
     */
    List<String> geoHash(M... members);

    /**
     * Get the {@link Point} representation of positions for one or more {@literal member}s.
     *
     * @param members must not be {@literal null}.
     * @return never {@literal null}.
     * @see <a href="http://redis.io/commands/geopos">Redis Documentation: GEOPOS</a>
     */
    List<Point> geoPos(M... members);

    /**
     * Get the {@literal member}s within the boundaries of a given {@link Circle}.
     *
     * @param within must not be {@literal null}.
     * @return never {@literal null}.
     * @see <a href="http://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
     */
    GeoResults<GeoLocation<M>> geoRadius(Circle within);

    /**
     * Get the {@literal member}s within the boundaries of a given {@link Circle} applying {@link GeoRadiusCommandArgs}.
     *
     * @param within must not be {@literal null}.
     * @param args must not be {@literal null}.
     * @return never {@literal null}.
     * @see <a href="http://redis.io/commands/georadius">Redis Documentation: GEORADIUS</a>
     */
    GeoResults<GeoLocation<M>> geoRadius(Circle within, GeoRadiusCommandArgs args);

    /**
     * Get the {@literal member}s within the circle defined by the {@literal members} coordinates and given
     * {@literal radius}.
     *
     * @param member must not be {@literal null}.
     * @param radius
     * @return never {@literal null}.
     * @see <a href="http://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
     */
    GeoResults<GeoLocation<M>> geoRadiusByMember(K key, M member, double radius);

    /**
     * Get the {@literal member}s within the circle defined by the {@literal members} coordinates and given
     * {@literal radius} applying {@link Metric}.
     *
     * @param member must not be {@literal null}.
     * @param distance must not be {@literal null}.
     * @return never {@literal null}.
     * @see <a href="http://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
     */
    GeoResults<GeoLocation<M>> geoRadiusByMember(M member, Distance distance);

    /**
     * Get the {@literal member}s within the circle defined by the {@literal members} coordinates and given
     * {@literal radius} applying {@link Metric} and {@link GeoRadiusCommandArgs}.
     *
     * @param member must not be {@literal null}.
     * @param distance must not be {@literal null}.
     * @param args must not be {@literal null}.
     * @return never {@literal null}.
     * @see <a href="http://redis.io/commands/georadiusbymember">Redis Documentation: GEORADIUSBYMEMBER</a>
     */
    GeoResults<GeoLocation<M>> geoRadiusByMember(M member, Distance distance, GeoRadiusCommandArgs args);

    /**
     * Remove the {@literal member}s.
     *
     * @param members must not be {@literal null}.
     * @return Number of elements removed.
     */
    Long geoRemove(M... members);
}

