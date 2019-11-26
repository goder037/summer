package com.rocket.summer.framework.data.redis.core.index;

import com.rocket.summer.framework.data.geo.Point;
import com.rocket.summer.framework.data.redis.connection.RedisGeoCommands.GeoLocation;

/**
 * @author Christoph Strobl
 * @since 1.8
 */
public class GeoIndexDefinition extends RedisIndexDefinition implements PathBasedRedisIndexDefinition {

    /**
     * Creates new {@link GeoIndexDefinition}.
     *
     * @param keyspace must not be {@literal null}.
     * @param path
     */
    public GeoIndexDefinition(String keyspace, String path) {
        this(keyspace, path, path);
    }

    /**
     * Creates new {@link GeoIndexDefinition}.
     *
     * @param keyspace must not be {@literal null}.
     * @param path
     * @param name must not be {@literal null}.
     */
    public GeoIndexDefinition(String keyspace, String path, String name) {
        super(keyspace, path, name);
        addCondition(new PathCondition(path));
        setValueTransformer(new PointValueTransformer());
    }

    /**
     * @author Christoph Strobl
     * @since 1.8
     */
    static class PointValueTransformer implements IndexValueTransformer {

        @Override
        public Point convert(Object source) {

            if (source == null || source instanceof Point) {
                return (Point) source;
            }

            if (source instanceof GeoLocation<?>) {
                return ((GeoLocation<?>) source).getPoint();
            }

            throw new IllegalArgumentException(
                    String.format("Cannot convert %s to %s. GeoIndexed property needs to be of type Point or GeoLocation!",
                            source.getClass(), Point.class));
        }
    }
}

