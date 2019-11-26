package com.rocket.summer.framework.data.redis.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rocket.summer.framework.dao.DataAccessException;
import com.rocket.summer.framework.data.geo.Circle;
import com.rocket.summer.framework.data.geo.GeoResult;
import com.rocket.summer.framework.data.geo.GeoResults;
import com.rocket.summer.framework.data.keyvalue.core.CriteriaAccessor;
import com.rocket.summer.framework.data.keyvalue.core.QueryEngine;
import com.rocket.summer.framework.data.keyvalue.core.SortAccessor;
import com.rocket.summer.framework.data.keyvalue.core.query.KeyValueQuery;
import com.rocket.summer.framework.data.redis.connection.RedisConnection;
import com.rocket.summer.framework.data.redis.connection.RedisGeoCommands.GeoLocation;
import com.rocket.summer.framework.data.redis.core.convert.GeoIndexedPropertyValue;
import com.rocket.summer.framework.data.redis.core.convert.RedisData;
import com.rocket.summer.framework.data.redis.repository.query.RedisOperationChain;
import com.rocket.summer.framework.data.redis.repository.query.RedisOperationChain.NearPath;
import com.rocket.summer.framework.data.redis.repository.query.RedisOperationChain.PathAndValue;
import com.rocket.summer.framework.data.redis.util.ByteUtils;
import com.rocket.summer.framework.util.CollectionUtils;

/**
 * Redis specific {@link QueryEngine} implementation.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @author Zhongning Fan
 * @since 1.7
 */
class RedisQueryEngine extends QueryEngine<RedisKeyValueAdapter, RedisOperationChain, Comparator<?>> {

    /**
     * Creates new {@link RedisQueryEngine} with defaults.
     */
    public RedisQueryEngine() {
        this(new RedisCriteriaAccessor(), null);
    }

    /**
     * Creates new {@link RedisQueryEngine}.
     *
     * @param criteriaAccessor
     * @param sortAccessor
     * @see QueryEngine#QueryEngine(CriteriaAccessor, SortAccessor)
     */
    public RedisQueryEngine(CriteriaAccessor<RedisOperationChain> criteriaAccessor,
                            SortAccessor<Comparator<?>> sortAccessor) {
        super(criteriaAccessor, sortAccessor);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.QueryEngine#execute(java.lang.Object, java.lang.Object, int, int, java.io.Serializable, java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> execute(final RedisOperationChain criteria, final Comparator<?> sort, final int offset,
                                     final int rows, final Serializable keyspace, Class<T> type) {

        if (criteria == null
                || (CollectionUtils.isEmpty(criteria.getOrSismember()) && CollectionUtils.isEmpty(criteria.getSismember()))
                && criteria.getNear() == null) {
            return (Collection<T>) getAdapter().getAllOf(keyspace, offset, rows);
        }

        RedisCallback<Map<byte[], Map<byte[], byte[]>>> callback = new RedisCallback<Map<byte[], Map<byte[], byte[]>>>() {

            @Override
            public Map<byte[], Map<byte[], byte[]>> doInRedis(RedisConnection connection) throws DataAccessException {

                List<byte[]> allKeys = new ArrayList<byte[]>();
                if (!criteria.getSismember().isEmpty()) {
                    allKeys.addAll(connection.sInter(keys(keyspace + ":", criteria.getSismember())));
                }

                if (!criteria.getOrSismember().isEmpty()) {
                    allKeys.addAll(connection.sUnion(keys(keyspace + ":", criteria.getOrSismember())));
                }

                if (criteria.getNear() != null) {

                    GeoResults<GeoLocation<byte[]>> x = connection.geoRadius(geoKey(keyspace + ":", criteria.getNear()),
                            new Circle(criteria.getNear().getPoint(), criteria.getNear().getDistance()));
                    for (GeoResult<GeoLocation<byte[]>> y : x) {
                        allKeys.add(y.getContent().getName());
                    }
                }

                byte[] keyspaceBin = getAdapter().getConverter().getConversionService().convert(keyspace + ":", byte[].class);

                final Map<byte[], Map<byte[], byte[]>> rawData = new LinkedHashMap<byte[], Map<byte[], byte[]>>();

                if (allKeys.isEmpty() || allKeys.size() < offset) {
                    return Collections.emptyMap();
                }

                int offsetToUse = Math.max(0, offset);
                if (rows > 0) {
                    allKeys = allKeys.subList(Math.max(0, offsetToUse), Math.min(offsetToUse + rows, allKeys.size()));
                }
                for (byte[] id : allKeys) {

                    byte[] singleKey = ByteUtils.concat(keyspaceBin, id);
                    rawData.put(id, connection.hGetAll(singleKey));
                }

                return rawData;

            }
        };

        Map<byte[], Map<byte[], byte[]>> raw = this.getAdapter().execute(callback);

        List<T> result = new ArrayList<T>(raw.size());
        for (Map.Entry<byte[], Map<byte[], byte[]>> entry : raw.entrySet()) {

            RedisData data = new RedisData(entry.getValue());
            data.setId(getAdapter().getConverter().getConversionService().convert(entry.getKey(), String.class));
            data.setKeyspace(keyspace.toString());

            T converted = this.getAdapter().getConverter().read(type, data);

            if (converted != null) {
                result.add(converted);
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.QueryEngine#execute(java.lang.Object, java.lang.Object, int, int, java.io.Serializable)
     */
    @Override
    public Collection<?> execute(final RedisOperationChain criteria, Comparator<?> sort, int offset, int rows,
                                 final Serializable keyspace) {
        return execute(criteria, sort, offset, rows, keyspace, Object.class);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.QueryEngine#count(java.lang.Object, java.io.Serializable)
     */
    @Override
    public long count(final RedisOperationChain criteria, final Serializable keyspace) {

        if (criteria == null) {
            return this.getAdapter().count(keyspace);
        }

        return this.getAdapter().execute(new RedisCallback<Long>() {

            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {

                long result = 0;

                if (!criteria.getOrSismember().isEmpty()) {
                    result += connection.sUnion(keys(keyspace + ":", criteria.getOrSismember())).size();
                }

                if (!criteria.getSismember().isEmpty()) {
                    result += connection.sInter(keys(keyspace + ":", criteria.getSismember())).size();
                }

                return result;
            }
        });
    }

    private byte[][] keys(String prefix, Collection<PathAndValue> source) {

        byte[][] keys = new byte[source.size()][];
        int i = 0;
        for (PathAndValue pathAndValue : source) {

            byte[] convertedValue = getAdapter().getConverter().getConversionService().convert(pathAndValue.getFirstValue(),
                    byte[].class);
            byte[] fullPath = getAdapter().getConverter().getConversionService()
                    .convert(prefix + pathAndValue.getPath() + ":", byte[].class);

            keys[i] = ByteUtils.concat(fullPath, convertedValue);
            i++;
        }
        return keys;
    }

    private byte[] geoKey(String prefix, NearPath source) {

        String path = GeoIndexedPropertyValue.geoIndexName(source.getPath());
        return getAdapter().getConverter().getConversionService().convert(prefix + path, byte[].class);

    }

    /**
     * @author Christoph Strobl
     * @since 1.7
     */
    static class RedisCriteriaAccessor implements CriteriaAccessor<RedisOperationChain> {

        @Override
        public RedisOperationChain resolve(KeyValueQuery<?> query) {
            return (RedisOperationChain) query.getCriteria();
        }
    }
}

