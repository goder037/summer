package com.rocket.summer.framework.data.redis.core.convert;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.StringUtils;

/**
 * Bucket is the data bag for Redis hash structures to be used with {@link RedisData}.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public class Bucket {

    /**
     * Encoding used for converting {@link Byte} to and from {@link String}.
     */
    public static final Charset CHARSET = Charset.forName("UTF-8");

    private final Map<String, byte[]> data;

    /**
     * Creates new empty bucket
     */
    public Bucket() {
        data = new LinkedHashMap<String, byte[]>();
    }

    Bucket(Map<String, byte[]> data) {

        Assert.notNull(data, "Inital data must not be null!");
        this.data = new LinkedHashMap<String, byte[]>(data.size());
        this.data.putAll(data);
    }

    /**
     * Add {@link String} representation of property dot path with given value.
     *
     * @param path must not be {@literal null} or {@link String#isEmpty()}.
     * @param value can be {@literal null}.
     */
    public void put(String path, byte[] value) {

        Assert.hasText(path, "Path to property must not be null or empty.");
        data.put(path, value);
    }

    /**
     * Get value assigned with path.
     *
     * @param path path must not be {@literal null} or {@link String#isEmpty()}.
     * @return {@literal null} if not set.
     */
    public byte[] get(String path) {

        Assert.hasText(path, "Path to property must not be null or empty.");
        return data.get(path);
    }

    /**
     * A set view of the mappings contained in this bucket.
     *
     * @return never {@literal null}.
     */
    public Set<Entry<String, byte[]>> entrySet() {
        return data.entrySet();
    }

    /**
     * @return {@literal true} when no data present in {@link Bucket}.
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     * @return the number of key-value mappings of the {@link Bucket}.
     */
    public int size() {
        return data.size();
    }

    /**
     * @return never {@literal null}.
     */
    public Collection<byte[]> values() {
        return data.values();
    }

    /**
     * @return never {@literal null}.
     */
    public Set<String> keySet() {
        return data.keySet();
    }

    /**
     * Key/value pairs contained in the {@link Bucket}.
     *
     * @return never {@literal null}.
     */
    public Map<String, byte[]> asMap() {
        return Collections.unmodifiableMap(this.data);
    }

    /**
     * Extracts a bucket containing key/value pairs with the {@code prefix}.
     *
     * @param prefix
     * @return
     */
    public Bucket extract(String prefix) {

        Bucket partial = new Bucket();
        for (Map.Entry<String, byte[]> entry : data.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                partial.put(entry.getKey(), entry.getValue());
            }
        }

        return partial;
    }

    /**
     * Get all the keys matching a given path.
     *
     * @param path the path to look for. Can be {@literal null}.
     * @return all keys if path is {@null} or empty.
     */
    public Set<String> extractAllKeysFor(String path) {

        if (!StringUtils.hasText(path)) {
            return keySet();
        }

        Pattern pattern = Pattern.compile("(" + Pattern.quote(path) + ")\\.\\[.*?\\]");

        Set<String> keys = new LinkedHashSet<String>();
        for (Map.Entry<String, byte[]> entry : data.entrySet()) {

            Matcher matcher = pattern.matcher(entry.getKey());
            if (matcher.find()) {
                keys.add(matcher.group());
            }
        }

        return keys;
    }

    /**
     * Get keys and values in binary format.
     *
     * @return never {@literal null}.
     */
    public Map<byte[], byte[]> rawMap() {

        Map<byte[], byte[]> raw = new LinkedHashMap<byte[], byte[]>(data.size());
        for (Map.Entry<String, byte[]> entry : data.entrySet()) {
            if (entry.getValue() != null) {
                raw.put(entry.getKey().getBytes(CHARSET), entry.getValue());
            }
        }
        return raw;
    }

    /**
     * Creates a new Bucket from a given raw map.
     *
     * @param source can be {@literal null}.
     * @return never {@literal null}.
     */
    public static Bucket newBucketFromRawMap(Map<byte[], byte[]> source) {

        Bucket bucket = new Bucket();
        if (source == null) {
            return bucket;
        }

        for (Map.Entry<byte[], byte[]> entry : source.entrySet()) {
            bucket.put(new String(entry.getKey(), CHARSET), entry.getValue());
        }
        return bucket;
    }

    /**
     * Creates a new Bucket from a given {@link String} map.
     *
     * @param source can be {@literal null}.
     * @return never {@literal null}.
     */
    public static Bucket newBucketFromStringMap(Map<String, String> source) {

        Bucket bucket = new Bucket();
        if (source == null) {
            return bucket;
        }

        for (Map.Entry<String, String> entry : source.entrySet()) {
            bucket.put(entry.getKey(), StringUtils.hasText(entry.getValue()) ? entry.getValue().getBytes(CHARSET)
                    : new byte[] {});
        }
        return bucket;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Bucket [data=" + safeToString() + "]";
    }

    private String safeToString() {

        Map<String, String> serialized = new LinkedHashMap<String, String>();
        for (Map.Entry<String, byte[]> entry : data.entrySet()) {
            if (entry.getValue() != null) {
                serialized.put(entry.getKey(), toUtf8String(entry.getValue()));
            } else {
                serialized.put(entry.getKey(), null);
            }
        }
        return serialized.toString();

    }

    private String toUtf8String(byte[] raw) {

        try {
            return new String(raw, CHARSET);
        } catch (Exception e) {
            // Ignore this one
        }
        return null;
    }

}

