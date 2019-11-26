package com.rocket.summer.framework.data.redis.core;

import java.util.Set;

import com.rocket.summer.framework.dao.InvalidDataAccessApiUsageException;
import com.rocket.summer.framework.data.redis.connection.DataType;
import com.rocket.summer.framework.data.redis.connection.RedisConnection;
import com.rocket.summer.framework.data.redis.core.convert.GeoIndexedPropertyValue;
import com.rocket.summer.framework.data.redis.core.convert.IndexedData;
import com.rocket.summer.framework.data.redis.core.convert.RedisConverter;
import com.rocket.summer.framework.data.redis.core.convert.RemoveIndexedData;
import com.rocket.summer.framework.data.redis.core.convert.SimpleIndexedPropertyValue;
import com.rocket.summer.framework.data.redis.util.ByteUtils;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.CollectionUtils;
import com.rocket.summer.framework.util.ObjectUtils;

/**
 * {@link IndexWriter} takes care of writing <a href="http://redis.io/topics/indexes">secondary index</a> structures to
 * Redis. Depending on the type of {@link IndexedData} it uses eg. Sets with specific names to add actually referenced
 * keys to. While doing so {@link IndexWriter} also keeps track of all indexes associated with the root types key, which
 * allows to remove the root key from all indexes in case of deletion.
 *
 * @author Christoph Strobl
 * @author Rob Winch
 * @since 1.7
 */
class IndexWriter {

    private final RedisConnection connection;
    private final RedisConverter converter;

    /**
     * Creates new {@link IndexWriter}.
     *
     * @param keyspace The key space to write index values to. Must not be {@literal null}.
     * @param connection must not be {@literal null}.
     * @param converter must not be {@literal null}.
     */
    public IndexWriter(RedisConnection connection, RedisConverter converter) {

        Assert.notNull(connection, "RedisConnection cannot be null!");
        Assert.notNull(converter, "RedisConverter cannot be null!");

        this.connection = connection;
        this.converter = converter;
    }

    /**
     * Initially creates indexes.
     *
     * @param key must not be {@literal null}.
     * @param indexValues can be {@literal null}.
     */
    public void createIndexes(Object key, Iterable<IndexedData> indexValues) {
        createOrUpdateIndexes(key, indexValues, IndexWriteMode.CREATE);
    }

    /**
     * Updates indexes by first removing key from existing one and then persisting new index data.
     *
     * @param key must not be {@literal null}.
     * @param indexValues can be {@literal null}.
     */
    public void updateIndexes(Object key, Iterable<IndexedData> indexValues) {
        createOrUpdateIndexes(key, indexValues, IndexWriteMode.PARTIAL_UPDATE);
    }

    /**
     * Updates indexes by first removing key from existing one and then persisting new index data.
     *
     * @param key must not be {@literal null}.
     * @param indexValues can be {@literal null}.
     */
    public void deleteAndUpdateIndexes(Object key, Iterable<IndexedData> indexValues) {
        createOrUpdateIndexes(key, indexValues, IndexWriteMode.UPDATE);
    }

    private void createOrUpdateIndexes(Object key, Iterable<IndexedData> indexValues, IndexWriteMode writeMode) {

        Assert.notNull(key, "Key must not be null!");
        if (indexValues == null) {
            return;
        }

        byte[] binKey = toBytes(key);

        if (ObjectUtils.nullSafeEquals(IndexWriteMode.UPDATE, writeMode)) {

            if (indexValues.iterator().hasNext()) {
                IndexedData data = indexValues.iterator().next();
                if (data != null && data.getKeyspace() != null) {
                    removeKeyFromIndexes(data.getKeyspace(), binKey);
                }
            }
        } else if (ObjectUtils.nullSafeEquals(IndexWriteMode.PARTIAL_UPDATE, writeMode)) {
            removeKeyFromExistingIndexes(binKey, indexValues);
        }

        addKeyToIndexes(binKey, indexValues);
    }

    /**
     * Removes a key from all available indexes.
     *
     * @param key must not be {@literal null}.
     */
    public void removeKeyFromIndexes(String keyspace, Object key) {

        Assert.notNull(key, "Key must not be null!");

        byte[] binKey = toBytes(key);
        byte[] indexHelperKey = ByteUtils.concatAll(toBytes(keyspace + ":"), binKey, toBytes(":idx"));

        for (byte[] indexKey : connection.sMembers(indexHelperKey)) {

            DataType type = connection.type(indexKey);
            if (DataType.ZSET.equals(type)) {
                connection.zRem(indexKey, binKey);
            } else {
                connection.sRem(indexKey, binKey);
            }
        }

        connection.del(indexHelperKey);
    }

    /**
     * Removes all indexes.
     */
    public void removeAllIndexes(String keyspace) {

        Set<byte[]> potentialIndex = connection.keys(toBytes(keyspace + ":*"));

        if (!potentialIndex.isEmpty()) {
            connection.del(potentialIndex.toArray(new byte[potentialIndex.size()][]));
        }
    }

    private void removeKeyFromExistingIndexes(byte[] key, Iterable<IndexedData> indexValues) {

        for (IndexedData indexData : indexValues) {
            removeKeyFromExistingIndexes(key, indexData);
        }
    }

    /**
     * Remove given key from all indexes matching {@link IndexedData#getIndexName()}:
     *
     * @param key
     * @param indexedData
     */
    protected void removeKeyFromExistingIndexes(byte[] key, IndexedData indexedData) {

        Assert.notNull(indexedData, "IndexedData must not be null!");

        Set<byte[]> existingKeys = connection
                .keys(toBytes(indexedData.getKeyspace() + ":" + indexedData.getIndexName() + ":*"));

        if (!CollectionUtils.isEmpty(existingKeys)) {
            for (byte[] existingKey : existingKeys) {

                if (indexedData instanceof GeoIndexedPropertyValue) {
                    connection.geoRemove(existingKey, key);
                } else {
                    connection.sRem(existingKey, key);
                }
            }
        }
    }

    private void addKeyToIndexes(byte[] key, Iterable<IndexedData> indexValues) {

        for (IndexedData indexData : indexValues) {
            addKeyToIndex(key, indexData);
        }
    }

    /**
     * Adds a given key to the index for {@link IndexedData#getIndexName()}.
     *
     * @param key must not be {@literal null}.
     * @param indexedData must not be {@literal null}.
     */
    protected void addKeyToIndex(byte[] key, IndexedData indexedData) {

        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(indexedData, "IndexedData must not be null!");

        if (indexedData instanceof RemoveIndexedData) {
            return;
        }

        if (indexedData instanceof SimpleIndexedPropertyValue) {

            Object value = ((SimpleIndexedPropertyValue) indexedData).getValue();

            if (value == null) {
                return;
            }

            byte[] indexKey = toBytes(indexedData.getKeyspace() + ":" + indexedData.getIndexName() + ":");
            indexKey = ByteUtils.concat(indexKey, toBytes(value));
            connection.sAdd(indexKey, key);

            // keep track of indexes used for the object
            connection.sAdd(ByteUtils.concatAll(toBytes(indexedData.getKeyspace() + ":"), key, toBytes(":idx")), indexKey);
        } else if (indexedData instanceof GeoIndexedPropertyValue) {

            GeoIndexedPropertyValue geoIndexedData = ((GeoIndexedPropertyValue) indexedData);

            Object value = geoIndexedData.getValue();
            if (value == null) {
                return;
            }

            byte[] indexKey = toBytes(indexedData.getKeyspace() + ":" + indexedData.getIndexName());
            connection.geoAdd(indexKey, geoIndexedData.getPoint(), key);

            // keep track of indexes used for the object
            connection.sAdd(ByteUtils.concatAll(toBytes(indexedData.getKeyspace() + ":"), key, toBytes(":idx")), indexKey);
        } else {
            throw new IllegalArgumentException(
                    String.format("Cannot write index data for unknown index type %s", indexedData.getClass()));
        }
    }

    private byte[] toBytes(Object source) {

        if (source == null) {
            return new byte[] {};
        }

        if (source instanceof byte[]) {
            return (byte[]) source;
        }

        if (converter.getConversionService().canConvert(source.getClass(), byte[].class)) {
            return converter.getConversionService().convert(source, byte[].class);
        }

        throw new InvalidDataAccessApiUsageException(String.format(
                "Cannot convert %s to binary representation for index key generation. "
                        + "Are you missing a Converter? Did you register a non PathBasedRedisIndexDefinition that might apply to a complex type?",
                source.getClass()));
    }

    /**
     * @author Christoph Strobl
     * @since 1.8
     */
    private static enum IndexWriteMode {

        CREATE, UPDATE, PARTIAL_UPDATE
    }
}

