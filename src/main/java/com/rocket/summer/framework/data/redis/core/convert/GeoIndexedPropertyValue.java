package com.rocket.summer.framework.data.redis.core.convert;

import com.rocket.summer.framework.data.geo.Point;

import lombok.Data;

/**
 * {@link IndexedData} implementation indicating storage of data within a Redis GEO structure.
 *
 * @author Christoph Strobl
 * @since 1.8
 */
@Data
public class GeoIndexedPropertyValue implements IndexedData {

    private final String keyspace;
    private final String indexName;
    private final Point value;

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.convert.IndexedData#getIndexName()
     */
    @Override
    public String getIndexName() {
        return GeoIndexedPropertyValue.geoIndexName(indexName);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.core.convert.IndexedData#getKeyspace()
     */
    @Override
    public String getKeyspace() {
        return keyspace;
    }

    public Point getPoint() {
        return value;
    }

    public static String geoIndexName(String path) {

        int index = path.lastIndexOf('.');
        if (index == -1) {
            return path;
        }
        StringBuilder sb = new StringBuilder(path);
        sb.setCharAt(index, ':');
        return sb.toString();
    }
}

