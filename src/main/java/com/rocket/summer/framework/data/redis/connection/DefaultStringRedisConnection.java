package com.rocket.summer.framework.data.redis.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.data.geo.Circle;
import com.rocket.summer.framework.data.geo.Distance;
import com.rocket.summer.framework.data.geo.GeoResults;
import com.rocket.summer.framework.data.geo.Metric;
import com.rocket.summer.framework.data.geo.Point;
import com.rocket.summer.framework.data.redis.RedisSystemException;
import com.rocket.summer.framework.data.redis.connection.convert.Converters;
import com.rocket.summer.framework.data.redis.connection.convert.ListConverter;
import com.rocket.summer.framework.data.redis.connection.convert.MapConverter;
import com.rocket.summer.framework.data.redis.connection.convert.SetConverter;
import com.rocket.summer.framework.data.redis.core.ConvertingCursor;
import com.rocket.summer.framework.data.redis.core.Cursor;
import com.rocket.summer.framework.data.redis.core.ScanOptions;
import com.rocket.summer.framework.data.redis.core.types.Expiration;
import com.rocket.summer.framework.data.redis.core.types.RedisClientInfo;
import com.rocket.summer.framework.data.redis.serializer.RedisSerializer;
import com.rocket.summer.framework.data.redis.serializer.StringRedisSerializer;
import com.rocket.summer.framework.util.Assert;

/**
 * Default implementation of {@link StringRedisConnection}.
 *
 * @author Costin Leau
 * @author Jennifer Hickey
 * @author Christoph Strobl
 * @author Thomas Darimont
 * @author Mark Paluch
 * @author Ninad Divadkar
 */
public class DefaultStringRedisConnection implements StringRedisConnection, DecoratedRedisConnection {

    private static final byte[][] EMPTY_2D_BYTE_ARRAY = new byte[0][];

    private final Log log = LogFactory.getLog(DefaultStringRedisConnection.class);
    private final RedisConnection delegate;
    private final RedisSerializer<String> serializer;
    private Converter<byte[], String> bytesToString = new DeserializingConverter();
    private SetConverter<Tuple, StringTuple> tupleToStringTuple = new SetConverter<Tuple, StringTuple>(
            new TupleConverter());
    private SetConverter<StringTuple, Tuple> stringTupleToTuple = new SetConverter<StringTuple, Tuple>(
            new StringTupleConverter());
    private ListConverter<byte[], String> byteListToStringList = new ListConverter<byte[], String>(bytesToString);
    private MapConverter<byte[], String> byteMapToStringMap = new MapConverter<byte[], String>(bytesToString);
    private SetConverter<byte[], String> byteSetToStringSet = new SetConverter<byte[], String>(bytesToString);
    private Converter<GeoResults<GeoLocation<byte[]>>, GeoResults<GeoLocation<String>>> byteGeoResultsToStringGeoResults;

    @SuppressWarnings("rawtypes") private Queue<Converter> pipelineConverters = new LinkedList<Converter>();
    @SuppressWarnings("rawtypes") private Queue<Converter> txConverters = new LinkedList<Converter>();
    private boolean deserializePipelineAndTxResults = false;
    private IdentityConverter identityConverter = new IdentityConverter();

    private class DeserializingConverter implements Converter<byte[], String> {
        public String convert(byte[] source) {
            return serializer.deserialize(source);
        }
    }

    private class TupleConverter implements Converter<Tuple, StringTuple> {
        public StringTuple convert(Tuple source) {
            return new DefaultStringTuple(source, serializer.deserialize(source.getValue()));
        }
    }

    private class StringTupleConverter implements Converter<StringTuple, Tuple> {
        public Tuple convert(StringTuple source) {
            return new DefaultTuple(source.getValue(), source.getScore());
        }
    }

    private class IdentityConverter implements Converter<Object, Object> {
        public Object convert(Object source) {
            return source;
        }
    }

    @SuppressWarnings("rawtypes")
    private class TransactionResultConverter implements Converter<List<Object>, List<Object>> {
        private Queue<Converter> txConverters;

        public TransactionResultConverter(Queue<Converter> txConverters) {
            this.txConverters = txConverters;
        }

        public List<Object> convert(List<Object> execResults) {
            return convertResults(execResults, txConverters);
        }
    }

    /**
     * Constructs a new <code>DefaultStringRedisConnection</code> instance. Uses {@link StringRedisSerializer} as
     * underlying serializer.
     *
     * @param connection Redis connection
     */
    public DefaultStringRedisConnection(RedisConnection connection) {
        this(connection, new StringRedisSerializer());
    }

    /**
     * Constructs a new <code>DefaultStringRedisConnection</code> instance.
     *
     * @param connection Redis connection
     * @param serializer String serializer
     */
    public DefaultStringRedisConnection(RedisConnection connection, RedisSerializer<String> serializer) {

        Assert.notNull(connection, "connection is required");
        Assert.notNull(serializer, "serializer is required");

        this.delegate = connection;
        this.serializer = serializer;
        this.byteGeoResultsToStringGeoResults = Converters.deserializingGeoResultsConverter(serializer);
    }

    public Long append(byte[] key, byte[] value) {
        Long result = delegate.append(key, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void bgSave() {
        delegate.bgSave();
    }

    @Override
    public void bgReWriteAof() {
        delegate.bgReWriteAof();
    }

    /**
     * @deprecated As of 1.3, use {@link #bgReWriteAof}.
     */
    @Deprecated
    public void bgWriteAof() {
        bgReWriteAof();
    }

    public List<byte[]> bLPop(int timeout, byte[]... keys) {
        List<byte[]> results = delegate.bLPop(timeout, keys);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public List<byte[]> bRPop(int timeout, byte[]... keys) {
        List<byte[]> results = delegate.bRPop(timeout, keys);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public byte[] bRPopLPush(int timeout, byte[] srcKey, byte[] dstKey) {
        byte[] result = delegate.bRPopLPush(timeout, srcKey, dstKey);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void close() throws RedisSystemException {
        delegate.close();
    }

    public Long dbSize() {
        Long result = delegate.dbSize();
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long decr(byte[] key) {
        Long result = delegate.decr(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long decrBy(byte[] key, long value) {
        Long result = delegate.decrBy(key, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long del(byte[]... keys) {
        Long result = delegate.del(keys);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void discard() {
        try {
            delegate.discard();
        } finally {
            txConverters.clear();
        }
    }

    public byte[] echo(byte[] message) {
        byte[] result = delegate.echo(message);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    public List<Object> exec() {
        try {
            List<Object> results = delegate.exec();
            if (isPipelined()) {
                pipelineConverters.add(new TransactionResultConverter(new LinkedList<Converter>(txConverters)));
                return results;
            }
            return convertResults(results, txConverters);
        } finally {
            txConverters.clear();
        }
    }

    public Boolean exists(byte[] key) {
        Boolean result = delegate.exists(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Boolean expire(byte[] key, long seconds) {
        Boolean result = delegate.expire(key, seconds);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Boolean expireAt(byte[] key, long unixTime) {
        Boolean result = delegate.expireAt(key, unixTime);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void flushAll() {
        delegate.flushAll();
    }

    public void flushDb() {
        delegate.flushDb();
    }

    public byte[] get(byte[] key) {
        byte[] result = delegate.get(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Boolean getBit(byte[] key, long offset) {
        Boolean result = delegate.getBit(key, offset);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public List<String> getConfig(String pattern) {
        List<String> results = delegate.getConfig(pattern);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Object getNativeConnection() {
        Object result = delegate.getNativeConnection();
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public byte[] getRange(byte[] key, long start, long end) {
        byte[] result = delegate.getRange(key, start, end);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public byte[] getSet(byte[] key, byte[] value) {
        byte[] result = delegate.getSet(key, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Subscription getSubscription() {
        return delegate.getSubscription();
    }

    public Long hDel(byte[] key, byte[]... fields) {
        Long result = delegate.hDel(key, fields);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Boolean hExists(byte[] key, byte[] field) {
        Boolean result = delegate.hExists(key, field);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public byte[] hGet(byte[] key, byte[] field) {
        byte[] result = delegate.hGet(key, field);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Map<byte[], byte[]> hGetAll(byte[] key) {
        Map<byte[], byte[]> results = delegate.hGetAll(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Long hIncrBy(byte[] key, byte[] field, long delta) {
        Long result = delegate.hIncrBy(key, field, delta);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Double hIncrBy(byte[] key, byte[] field, double delta) {
        Double result = delegate.hIncrBy(key, field, delta);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Set<byte[]> hKeys(byte[] key) {
        Set<byte[]> results = delegate.hKeys(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Long hLen(byte[] key) {
        Long result = delegate.hLen(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public List<byte[]> hMGet(byte[] key, byte[]... fields) {
        List<byte[]> results = delegate.hMGet(key, fields);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public void hMSet(byte[] key, Map<byte[], byte[]> hashes) {
        delegate.hMSet(key, hashes);
    }

    public Boolean hSet(byte[] key, byte[] field, byte[] value) {
        Boolean result = delegate.hSet(key, field, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Boolean hSetNX(byte[] key, byte[] field, byte[] value) {
        Boolean result = delegate.hSetNX(key, field, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public List<byte[]> hVals(byte[] key) {
        List<byte[]> results = delegate.hVals(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Long incr(byte[] key) {
        Long result = delegate.incr(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long incrBy(byte[] key, long value) {
        Long result = delegate.incrBy(key, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Double incrBy(byte[] key, double value) {
        Double result = delegate.incrBy(key, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Properties info() {
        Properties result = delegate.info();
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Properties info(String section) {
        Properties result = delegate.info(section);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public boolean isClosed() {
        return delegate.isClosed();
    }

    public boolean isQueueing() {
        return delegate.isQueueing();
    }

    public boolean isSubscribed() {
        return delegate.isSubscribed();
    }

    public Set<byte[]> keys(byte[] pattern) {
        Set<byte[]> results = delegate.keys(pattern);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Long lastSave() {
        Long result = delegate.lastSave();
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public byte[] lIndex(byte[] key, long index) {
        byte[] result = delegate.lIndex(key, index);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long lInsert(byte[] key, Position where, byte[] pivot, byte[] value) {
        Long result = delegate.lInsert(key, where, pivot, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long lLen(byte[] key) {
        Long result = delegate.lLen(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public byte[] lPop(byte[] key) {
        byte[] result = delegate.lPop(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long lPush(byte[] key, byte[]... values) {
        Long result = delegate.lPush(key, values);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long lPushX(byte[] key, byte[] value) {
        Long result = delegate.lPushX(key, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public List<byte[]> lRange(byte[] key, long start, long end) {
        List<byte[]> results = delegate.lRange(key, start, end);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Long lRem(byte[] key, long count, byte[] value) {
        Long result = delegate.lRem(key, count, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void lSet(byte[] key, long index, byte[] value) {
        delegate.lSet(key, index, value);
    }

    public void lTrim(byte[] key, long start, long end) {
        delegate.lTrim(key, start, end);
    }

    public List<byte[]> mGet(byte[]... keys) {
        List<byte[]> results = delegate.mGet(keys);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public void mSet(Map<byte[], byte[]> tuple) {
        delegate.mSet(tuple);
    }

    public Boolean mSetNX(Map<byte[], byte[]> tuple) {
        Boolean result = delegate.mSetNX(tuple);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void multi() {
        delegate.multi();
    }

    public Boolean persist(byte[] key) {
        Boolean result = delegate.persist(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Boolean move(byte[] key, int dbIndex) {
        Boolean result = delegate.move(key, dbIndex);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public String ping() {
        String result = delegate.ping();
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void pSubscribe(MessageListener listener, byte[]... patterns) {
        delegate.pSubscribe(listener, patterns);
    }

    public Long publish(byte[] channel, byte[] message) {
        Long result = delegate.publish(channel, message);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public byte[] randomKey() {
        byte[] result = delegate.randomKey();
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void rename(byte[] oldName, byte[] newName) {
        delegate.rename(oldName, newName);
    }

    public Boolean renameNX(byte[] oldName, byte[] newName) {
        Boolean result = delegate.renameNX(oldName, newName);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void resetConfigStats() {
        delegate.resetConfigStats();
    }

    public byte[] rPop(byte[] key) {
        byte[] result = delegate.rPop(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public byte[] rPopLPush(byte[] srcKey, byte[] dstKey) {
        byte[] result = delegate.rPopLPush(srcKey, dstKey);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long rPush(byte[] key, byte[]... values) {
        Long result = delegate.rPush(key, values);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long rPushX(byte[] key, byte[] value) {
        Long result = delegate.rPushX(key, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long sAdd(byte[] key, byte[]... values) {
        Long result = delegate.sAdd(key, values);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void save() {
        delegate.save();
    }

    public Long sCard(byte[] key) {
        Long result = delegate.sCard(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Set<byte[]> sDiff(byte[]... keys) {
        Set<byte[]> results = delegate.sDiff(keys);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Long sDiffStore(byte[] destKey, byte[]... keys) {
        Long result = delegate.sDiffStore(destKey, keys);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void select(int dbIndex) {
        delegate.select(dbIndex);
    }

    public void set(byte[] key, byte[] value) {
        delegate.set(key, value);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisStringCommands#set(byte[], byte[], com.rocket.summer.framework.data.redis.core.types.Expiration, com.rocket.summer.framework.data.redis.connection.RedisStringCommands.SetOptions)
     */
    @Override
    public void set(byte[] key, byte[] value, Expiration expiration, SetOption option) {
        delegate.set(key, value, expiration, option);
    }

    public Boolean setBit(byte[] key, long offset, boolean value) {
        return delegate.setBit(key, offset, value);
    }

    public void setConfig(String param, String value) {
        delegate.setConfig(param, value);
    }

    public void setEx(byte[] key, long seconds, byte[] value) {
        delegate.setEx(key, seconds, value);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisStringCommands#pSetEx(byte[], long, byte[])
     */
    @Override
    public void pSetEx(byte[] key, long milliseconds, byte[] value) {
        delegate.pSetEx(key, milliseconds, value);
    }

    public Boolean setNX(byte[] key, byte[] value) {
        Boolean result = delegate.setNX(key, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void setRange(byte[] key, byte[] value, long start) {
        delegate.setRange(key, value, start);
    }

    public void shutdown() {
        delegate.shutdown();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisServerCommands#shutdown(com.rocket.summer.framework.data.redis.connection.RedisServerCommands.ShutdownOption)
     */
    @Override
    public void shutdown(ShutdownOption option) {
        delegate.shutdown(option);
    }

    public Set<byte[]> sInter(byte[]... keys) {
        Set<byte[]> results = delegate.sInter(keys);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Long sInterStore(byte[] destKey, byte[]... keys) {
        Long result = delegate.sInterStore(destKey, keys);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Boolean sIsMember(byte[] key, byte[] value) {
        Boolean result = delegate.sIsMember(key, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Set<byte[]> sMembers(byte[] key) {
        Set<byte[]> results = delegate.sMembers(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Boolean sMove(byte[] srcKey, byte[] destKey, byte[] value) {
        Boolean result = delegate.sMove(srcKey, destKey, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long sort(byte[] key, SortParameters params, byte[] storeKey) {
        Long result = delegate.sort(key, params, storeKey);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public List<byte[]> sort(byte[] key, SortParameters params) {
        List<byte[]> results = delegate.sort(key, params);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public byte[] sPop(byte[] key) {
        byte[] result = delegate.sPop(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public byte[] sRandMember(byte[] key) {
        byte[] result = delegate.sRandMember(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public List<byte[]> sRandMember(byte[] key, long count) {
        List<byte[]> results = delegate.sRandMember(key, count);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Long sRem(byte[] key, byte[]... values) {
        Long result = delegate.sRem(key, values);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long strLen(byte[] key) {
        Long result = delegate.strLen(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long bitCount(byte[] key) {
        Long result = delegate.bitCount(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long bitCount(byte[] key, long begin, long end) {
        Long result = delegate.bitCount(key, begin, end);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long bitOp(BitOperation op, byte[] destination, byte[]... keys) {
        Long result = delegate.bitOp(op, destination, keys);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void subscribe(MessageListener listener, byte[]... channels) {
        delegate.subscribe(listener, channels);
    }

    public Set<byte[]> sUnion(byte[]... keys) {
        Set<byte[]> results = delegate.sUnion(keys);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Long sUnionStore(byte[] destKey, byte[]... keys) {
        Long result = delegate.sUnionStore(destKey, keys);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisKeyCommands#ttl(byte[])
     */
    @Override
    public Long ttl(byte[] key) {

        Long result = delegate.ttl(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisKeyCommands#ttl(byte[], java.util.concurrent.TimeUnit)
     */
    @Override
    public Long ttl(byte[] key, TimeUnit timeUnit) {

        Long result = delegate.ttl(key, timeUnit);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }

        return result;
    }

    public DataType type(byte[] key) {
        DataType result = delegate.type(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void unwatch() {
        delegate.unwatch();
    }

    public void watch(byte[]... keys) {
        delegate.watch(keys);
    }

    public Boolean zAdd(byte[] key, double score, byte[] value) {
        Boolean result = delegate.zAdd(key, score, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zAdd(byte[] key, Set<Tuple> tuples) {
        Long result = delegate.zAdd(key, tuples);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zCard(byte[] key) {
        Long result = delegate.zCard(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zCount(byte[] key, double min, double max) {
        Long result = delegate.zCount(key, min, max);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisZSetCommands#zCount(byte[], com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Range)
     */
    @Override
    public Long zCount(byte[] key, Range range) {

        Long result = delegate.zCount(key, range);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Double zIncrBy(byte[] key, double increment, byte[] value) {
        Double result = delegate.zIncrBy(key, increment, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zInterStore(byte[] destKey, Aggregate aggregate, int[] weights, byte[]... sets) {
        Long result = delegate.zInterStore(destKey, aggregate, weights, sets);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zInterStore(byte[] destKey, byte[]... sets) {
        Long result = delegate.zInterStore(destKey, sets);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Set<byte[]> zRange(byte[] key, long start, long end) {
        Set<byte[]> results = delegate.zRange(key, start, end);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Set<byte[]> zRangeByScore(byte[] key, double min, double max, long offset, long count) {
        Set<byte[]> results = delegate.zRangeByScore(key, min, max, offset, count);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisZSetCommands#zRangeByScore(byte[], com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Range)
     */
    @Override
    public Set<byte[]> zRangeByScore(byte[] key, Range range) {

        Set<byte[]> results = delegate.zRangeByScore(key, range);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisZSetCommands#zRangeByScore(byte[], com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Range, com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Limit)
     */
    @Override
    public Set<byte[]> zRangeByScore(byte[] key, Range range, Limit limit) {

        Set<byte[]> results = delegate.zRangeByScore(key, range, limit);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisZSetCommands#zRangeByScoreWithScores(byte[], com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Range)
     */
    @Override
    public Set<Tuple> zRangeByScoreWithScores(byte[] key, Range range) {

        Set<Tuple> results = delegate.zRangeByScoreWithScores(key, range);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Set<byte[]> zRangeByScore(byte[] key, double min, double max) {
        Set<byte[]> results = delegate.zRangeByScore(key, min, max);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Set<Tuple> zRangeByScoreWithScores(byte[] key, double min, double max, long offset, long count) {
        Set<Tuple> results = delegate.zRangeByScoreWithScores(key, min, max, offset, count);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisZSetCommands#zRangeByScoreWithScores(byte[], com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Range, com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Limit)
     */
    @Override
    public Set<Tuple> zRangeByScoreWithScores(byte[] key, Range range, Limit limit) {

        Set<Tuple> results = delegate.zRangeByScoreWithScores(key, range, limit);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Set<Tuple> zRangeByScoreWithScores(byte[] key, double min, double max) {
        Set<Tuple> results = delegate.zRangeByScoreWithScores(key, min, max);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Set<Tuple> zRangeWithScores(byte[] key, long start, long end) {
        Set<Tuple> results = delegate.zRangeWithScores(key, start, end);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Set<byte[]> zRevRangeByScore(byte[] key, double min, double max, long offset, long count) {
        Set<byte[]> results = delegate.zRevRangeByScore(key, min, max, offset, count);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisZSetCommands#zRevRangeByScore(byte[], com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Range)
     */
    @Override
    public Set<byte[]> zRevRangeByScore(byte[] key, Range range) {

        Set<byte[]> results = delegate.zRevRangeByScore(key, range);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Set<byte[]> zRevRangeByScore(byte[] key, double min, double max) {
        Set<byte[]> results = delegate.zRevRangeByScore(key, min, max);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisZSetCommands#zRevRangeByScore(byte[], com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Range, com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Limit)
     */
    @Override
    public Set<byte[]> zRevRangeByScore(byte[] key, Range range, Limit limit) {

        Set<byte[]> results = delegate.zRevRangeByScore(key, range, limit);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Set<Tuple> zRevRangeByScoreWithScores(byte[] key, double min, double max, long offset, long count) {
        Set<Tuple> results = delegate.zRevRangeByScoreWithScores(key, min, max, offset, count);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisZSetCommands#zRevRangeByScoreWithScores(byte[], com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Range)
     */
    @Override
    public Set<Tuple> zRevRangeByScoreWithScores(byte[] key, Range range) {

        Set<Tuple> results = delegate.zRevRangeByScoreWithScores(key, range);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisZSetCommands#zRevRangeByScoreWithScores(byte[], com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Range, com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Limit)
     */
    @Override
    public Set<Tuple> zRevRangeByScoreWithScores(byte[] key, Range range, Limit limit) {

        Set<Tuple> results = delegate.zRevRangeByScoreWithScores(key, range, limit);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Set<Tuple> zRevRangeByScoreWithScores(byte[] key, double min, double max) {
        Set<Tuple> results = delegate.zRevRangeByScoreWithScores(key, min, max);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Long zRank(byte[] key, byte[] value) {
        Long result = delegate.zRank(key, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zRem(byte[] key, byte[]... values) {
        Long result = delegate.zRem(key, values);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zRemRange(byte[] key, long start, long end) {
        Long result = delegate.zRemRange(key, start, end);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zRemRangeByScore(byte[] key, double min, double max) {
        Long result = delegate.zRemRangeByScore(key, min, max);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    @Override
    public Long zRemRangeByScore(byte[] key, Range range) {
        Long result = delegate.zRemRangeByScore(key, range);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Set<byte[]> zRevRange(byte[] key, long start, long end) {
        Set<byte[]> results = delegate.zRevRange(key, start, end);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Set<Tuple> zRevRangeWithScores(byte[] key, long start, long end) {
        Set<Tuple> results = delegate.zRevRangeWithScores(key, start, end);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public Long zRevRank(byte[] key, byte[] value) {
        Long result = delegate.zRevRank(key, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Double zScore(byte[] key, byte[] value) {
        Double result = delegate.zScore(key, value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zUnionStore(byte[] destKey, Aggregate aggregate, int[] weights, byte[]... sets) {
        Long result = delegate.zUnionStore(destKey, aggregate, weights, sets);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zUnionStore(byte[] destKey, byte[]... sets) {
        Long result = delegate.zUnionStore(destKey, sets);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Boolean pExpire(byte[] key, long millis) {
        Boolean result = delegate.pExpire(key, millis);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Boolean pExpireAt(byte[] key, long unixTimeInMillis) {
        Boolean result = delegate.pExpireAt(key, unixTimeInMillis);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisKeyCommands#pTtl(byte[])
     */
    @Override
    public Long pTtl(byte[] key) {

        Long result = delegate.pTtl(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisKeyCommands#pTtl(byte[], java.util.concurrent.TimeUnit)
     */
    @Override
    public Long pTtl(byte[] key, TimeUnit timeUnit) {

        Long result = delegate.pTtl(key, timeUnit);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }

        return result;
    }

    public byte[] dump(byte[] key) {
        byte[] result = delegate.dump(key);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void restore(byte[] key, long ttlInMillis, byte[] serializedValue) {
        delegate.restore(key, ttlInMillis, serializedValue);
    }

    public void scriptFlush() {
        delegate.scriptFlush();
    }

    public void scriptKill() {
        delegate.scriptKill();
    }

    public String scriptLoad(byte[] script) {
        String result = delegate.scriptLoad(script);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public List<Boolean> scriptExists(String... scriptSha1) {
        List<Boolean> results = delegate.scriptExists(scriptSha1);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    public <T> T eval(byte[] script, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        T result = delegate.eval(script, returnType, numKeys, keysAndArgs);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public <T> T evalSha(String scriptSha1, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        T result = delegate.evalSha(scriptSha1, returnType, numKeys, keysAndArgs);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public <T> T evalSha(byte[] scriptSha1, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        T result = delegate.evalSha(scriptSha1, returnType, numKeys, keysAndArgs);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    //
    // String methods
    //

    private byte[] serialize(String data) {
        return serializer.serialize(data);
    }

    private byte[][] serializeMulti(String... keys) {

        if (keys == null) {
            return EMPTY_2D_BYTE_ARRAY;
        }

        byte[][] ret = new byte[keys.length][];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = serializer.serialize(keys[i]);
        }

        return ret;
    }

    private Map<byte[], byte[]> serialize(Map<String, String> hashes) {
        Map<byte[], byte[]> ret = new LinkedHashMap<byte[], byte[]>(hashes.size());

        for (Map.Entry<String, String> entry : hashes.entrySet()) {
            ret.put(serializer.serialize(entry.getKey()), serializer.serialize(entry.getValue()));
        }

        return ret;
    }

    public Long append(String key, String value) {
        Long result = delegate.append(serialize(key), serialize(value));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public List<String> bLPop(int timeout, String... keys) {
        List<byte[]> results = delegate.bLPop(timeout, serializeMulti(keys));
        if (isFutureConversion()) {
            addResultConverter(byteListToStringList);
        }
        return byteListToStringList.convert(results);
    }

    public List<String> bRPop(int timeout, String... keys) {
        List<byte[]> results = delegate.bRPop(timeout, serializeMulti(keys));
        if (isFutureConversion()) {
            addResultConverter(byteListToStringList);
        }
        return byteListToStringList.convert(results);
    }

    public String bRPopLPush(int timeout, String srcKey, String dstKey) {
        byte[] result = delegate.bRPopLPush(timeout, serialize(srcKey), serialize(dstKey));
        if (isFutureConversion()) {
            addResultConverter(bytesToString);
        }
        return bytesToString.convert(result);
    }

    public Long decr(String key) {
        Long result = delegate.decr(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long decrBy(String key, long value) {
        Long result = delegate.decrBy(serialize(key), value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long del(String... keys) {
        Long result = delegate.del(serializeMulti(keys));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public String echo(String message) {
        byte[] result = delegate.echo(serialize(message));
        if (isFutureConversion()) {
            addResultConverter(bytesToString);
        }
        return bytesToString.convert(result);
    }

    public Boolean exists(String key) {
        Boolean result = delegate.exists(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Boolean expire(String key, long seconds) {
        Boolean result = delegate.expire(serialize(key), seconds);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Boolean expireAt(String key, long unixTime) {
        Boolean result = delegate.expireAt(serialize(key), unixTime);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public String get(String key) {
        byte[] result = delegate.get(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(bytesToString);
        }
        return bytesToString.convert(result);
    }

    public Boolean getBit(String key, long offset) {
        Boolean result = delegate.getBit(serialize(key), offset);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public String getRange(String key, long start, long end) {
        byte[] result = delegate.getRange(serialize(key), start, end);
        if (isFutureConversion()) {
            addResultConverter(bytesToString);
        }
        return bytesToString.convert(result);
    }

    public String getSet(String key, String value) {
        byte[] result = delegate.getSet(serialize(key), serialize(value));
        if (isFutureConversion()) {
            addResultConverter(bytesToString);
        }
        return bytesToString.convert(result);
    }

    public Long hDel(String key, String... fields) {
        Long result = delegate.hDel(serialize(key), serializeMulti(fields));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Boolean hExists(String key, String field) {
        Boolean result = delegate.hExists(serialize(key), serialize(field));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public String hGet(String key, String field) {
        byte[] result = delegate.hGet(serialize(key), serialize(field));
        if (isFutureConversion()) {
            addResultConverter(bytesToString);
        }
        return bytesToString.convert(result);
    }

    public Map<String, String> hGetAll(String key) {
        Map<byte[], byte[]> results = delegate.hGetAll(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(byteMapToStringMap);
        }
        return byteMapToStringMap.convert(results);
    }

    public Long hIncrBy(String key, String field, long delta) {
        Long result = delegate.hIncrBy(serialize(key), serialize(field), delta);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Double hIncrBy(String key, String field, double delta) {
        Double result = delegate.hIncrBy(serialize(key), serialize(field), delta);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Set<String> hKeys(String key) {
        Set<byte[]> results = delegate.hKeys(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(byteSetToStringSet);
        }
        return byteSetToStringSet.convert(results);
    }

    public Long hLen(String key) {
        Long result = delegate.hLen(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public List<String> hMGet(String key, String... fields) {
        List<byte[]> results = delegate.hMGet(serialize(key), serializeMulti(fields));
        if (isFutureConversion()) {
            addResultConverter(byteListToStringList);
        }
        return byteListToStringList.convert(results);
    }

    public void hMSet(String key, Map<String, String> hashes) {
        delegate.hMSet(serialize(key), serialize(hashes));
    }

    public Boolean hSet(String key, String field, String value) {
        Boolean result = delegate.hSet(serialize(key), serialize(field), serialize(value));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Boolean hSetNX(String key, String field, String value) {
        Boolean result = delegate.hSetNX(serialize(key), serialize(field), serialize(value));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public List<String> hVals(String key) {
        List<byte[]> results = delegate.hVals(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(byteListToStringList);
        }
        return byteListToStringList.convert(results);
    }

    public Long incr(String key) {
        Long result = delegate.incr(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long incrBy(String key, long value) {
        Long result = delegate.incrBy(serialize(key), value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Double incrBy(String key, double value) {
        Double result = delegate.incrBy(serialize(key), value);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Collection<String> keys(String pattern) {
        Set<byte[]> results = delegate.keys(serialize(pattern));
        if (isFutureConversion()) {
            addResultConverter(byteSetToStringSet);
        }
        return byteSetToStringSet.convert(results);
    }

    public String lIndex(String key, long index) {
        byte[] result = delegate.lIndex(serialize(key), index);
        if (isFutureConversion()) {
            addResultConverter(bytesToString);
        }
        return bytesToString.convert(result);
    }

    public Long lInsert(String key, Position where, String pivot, String value) {
        Long result = delegate.lInsert(serialize(key), where, serialize(pivot), serialize(value));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long lLen(String key) {
        Long result = delegate.lLen(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public String lPop(String key) {
        byte[] result = delegate.lPop(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(bytesToString);
        }
        return bytesToString.convert(result);
    }

    public Long lPush(String key, String... values) {
        Long result = delegate.lPush(serialize(key), serializeMulti(values));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long lPushX(String key, String value) {
        Long result = delegate.lPushX(serialize(key), serialize(value));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public List<String> lRange(String key, long start, long end) {
        List<byte[]> results = delegate.lRange(serialize(key), start, end);
        if (isFutureConversion()) {
            addResultConverter(byteListToStringList);
        }
        return byteListToStringList.convert(results);
    }

    public Long lRem(String key, long count, String value) {
        Long result = delegate.lRem(serialize(key), count, serialize(value));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void lSet(String key, long index, String value) {
        delegate.lSet(serialize(key), index, serialize(value));
    }

    public void lTrim(String key, long start, long end) {
        delegate.lTrim(serialize(key), start, end);
    }

    public List<String> mGet(String... keys) {
        List<byte[]> results = delegate.mGet(serializeMulti(keys));
        if (isFutureConversion()) {
            addResultConverter(byteListToStringList);
        }
        return byteListToStringList.convert(results);
    }

    public Boolean mSetNXString(Map<String, String> tuple) {
        Boolean result = delegate.mSetNX(serialize(tuple));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void mSetString(Map<String, String> tuple) {
        delegate.mSet(serialize(tuple));
    }

    public Boolean persist(String key) {
        Boolean result = delegate.persist(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Boolean move(String key, int dbIndex) {
        Boolean result = delegate.move(serialize(key), dbIndex);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void pSubscribe(MessageListener listener, String... patterns) {
        delegate.pSubscribe(listener, serializeMulti(patterns));
    }

    public Long publish(String channel, String message) {
        Long result = delegate.publish(serialize(channel), serialize(message));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void rename(String oldName, String newName) {
        delegate.rename(serialize(oldName), serialize(newName));
    }

    public Boolean renameNX(String oldName, String newName) {
        Boolean result = delegate.renameNX(serialize(oldName), serialize(newName));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public String rPop(String key) {
        byte[] result = delegate.rPop(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(bytesToString);
        }
        return bytesToString.convert(result);
    }

    public String rPopLPush(String srcKey, String dstKey) {
        byte[] result = delegate.rPopLPush(serialize(srcKey), serialize(dstKey));
        if (isFutureConversion()) {
            addResultConverter(bytesToString);
        }
        return bytesToString.convert(result);
    }

    public Long rPush(String key, String... values) {
        Long result = delegate.rPush(serialize(key), serializeMulti(values));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long rPushX(String key, String value) {
        Long result = delegate.rPushX(serialize(key), serialize(value));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long sAdd(String key, String... values) {
        Long result = delegate.sAdd(serialize(key), serializeMulti(values));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long sCard(String key) {
        Long result = delegate.sCard(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Set<String> sDiff(String... keys) {
        Set<byte[]> results = delegate.sDiff(serializeMulti(keys));
        if (isFutureConversion()) {
            addResultConverter(byteSetToStringSet);
        }
        return byteSetToStringSet.convert(results);
    }

    public Long sDiffStore(String destKey, String... keys) {
        Long result = delegate.sDiffStore(serialize(destKey), serializeMulti(keys));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void set(String key, String value) {
        delegate.set(serialize(key), serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#set(java.lang.String, java.lang.String, com.rocket.summer.framework.data.redis.core.types.Expiration, com.rocket.summer.framework.data.redis.connection.RedisStringCommands.SetOptions)
     */
    public void set(String key, String value, Expiration expiration, SetOption option) {
        set(serialize(key), serialize(value), expiration, option);
    }

    public Boolean setBit(String key, long offset, boolean value) {
        return delegate.setBit(serialize(key), offset, value);
    }

    public void setEx(String key, long seconds, String value) {
        delegate.setEx(serialize(key), seconds, serialize(value));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#pSetEx(java.lang.String, long, java.lang.String)
     */
    @Override
    public void pSetEx(String key, long seconds, String value) {
        pSetEx(serialize(key), seconds, serialize(value));
    }

    public Boolean setNX(String key, String value) {
        Boolean result = delegate.setNX(serialize(key), serialize(value));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void setRange(String key, String value, long start) {
        delegate.setRange(serialize(key), serialize(value), start);
    }

    public Set<String> sInter(String... keys) {
        Set<byte[]> results = delegate.sInter(serializeMulti(keys));
        if (isFutureConversion()) {
            addResultConverter(byteSetToStringSet);
        }
        return byteSetToStringSet.convert(results);
    }

    public Long sInterStore(String destKey, String... keys) {
        Long result = delegate.sInterStore(serialize(destKey), serializeMulti(keys));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Boolean sIsMember(String key, String value) {
        Boolean result = delegate.sIsMember(serialize(key), serialize(value));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Set<String> sMembers(String key) {
        Set<byte[]> results = delegate.sMembers(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(byteSetToStringSet);
        }
        return byteSetToStringSet.convert(results);
    }

    public Boolean sMove(String srcKey, String destKey, String value) {
        Boolean result = delegate.sMove(serialize(srcKey), serialize(destKey), serialize(value));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long sort(String key, SortParameters params, String storeKey) {
        Long result = delegate.sort(serialize(key), params, serialize(storeKey));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public List<String> sort(String key, SortParameters params) {
        List<byte[]> results = delegate.sort(serialize(key), params);
        if (isFutureConversion()) {
            addResultConverter(byteListToStringList);
        }
        return byteListToStringList.convert(results);
    }

    public String sPop(String key) {
        byte[] result = delegate.sPop(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(bytesToString);
        }
        return bytesToString.convert(result);
    }

    public String sRandMember(String key) {
        byte[] result = delegate.sRandMember(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(bytesToString);
        }
        return bytesToString.convert(result);
    }

    public List<String> sRandMember(String key, long count) {
        List<byte[]> results = delegate.sRandMember(serialize(key), count);
        if (isFutureConversion()) {
            addResultConverter(byteListToStringList);
        }
        return byteListToStringList.convert(results);
    }

    public Long sRem(String key, String... values) {
        Long result = delegate.sRem(serialize(key), serializeMulti(values));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long strLen(String key) {
        Long result = delegate.strLen(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long bitCount(String key) {
        Long result = delegate.bitCount(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long bitCount(String key, long begin, long end) {
        Long result = delegate.bitCount(serialize(key), begin, end);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long bitOp(BitOperation op, String destination, String... keys) {
        Long result = delegate.bitOp(op, serialize(destination), serializeMulti(keys));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public void subscribe(MessageListener listener, String... channels) {
        delegate.subscribe(listener, serializeMulti(channels));
    }

    public Set<String> sUnion(String... keys) {
        Set<byte[]> results = delegate.sUnion(serializeMulti(keys));
        if (isFutureConversion()) {
            addResultConverter(byteSetToStringSet);
        }
        return byteSetToStringSet.convert(results);
    }

    public Long sUnionStore(String destKey, String... keys) {
        Long result = delegate.sUnionStore(serialize(destKey), serializeMulti(keys));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#ttl(java.lang.String)
     */
    @Override
    public Long ttl(String key) {
        return ttl(serialize(key));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#ttl(java.lang.String, java.util.concurrent.TimeUnit)
     */
    @Override
    public Long ttl(String key, TimeUnit timeUnit) {
        return ttl(serialize(key), timeUnit);
    }

    public DataType type(String key) {
        DataType result = delegate.type(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Boolean zAdd(String key, double score, String value) {
        Boolean result = delegate.zAdd(serialize(key), score, serialize(value));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zAdd(String key, Set<StringTuple> tuples) {
        Long result = delegate.zAdd(serialize(key), stringTupleToTuple.convert(tuples));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zCard(String key) {
        Long result = delegate.zCard(serialize(key));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zCount(String key, double min, double max) {
        Long result = delegate.zCount(serialize(key), min, max);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Double zIncrBy(String key, double increment, String value) {
        Double result = delegate.zIncrBy(serialize(key), increment, serialize(value));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zInterStore(String destKey, Aggregate aggregate, int[] weights, String... sets) {
        Long result = delegate.zInterStore(serialize(destKey), aggregate, weights, serializeMulti(sets));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zInterStore(String destKey, String... sets) {
        Long result = delegate.zInterStore(serialize(destKey), serializeMulti(sets));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Set<String> zRange(String key, long start, long end) {
        Set<byte[]> results = delegate.zRange(serialize(key), start, end);
        if (isFutureConversion()) {
            addResultConverter(byteSetToStringSet);
        }
        return byteSetToStringSet.convert(results);
    }

    public Set<String> zRangeByScore(String key, double min, double max, long offset, long count) {
        Set<byte[]> results = delegate.zRangeByScore(serialize(key), min, max, offset, count);
        if (isFutureConversion()) {
            addResultConverter(byteSetToStringSet);
        }
        return byteSetToStringSet.convert(results);
    }

    public Set<String> zRangeByScore(String key, double min, double max) {
        Set<byte[]> results = delegate.zRangeByScore(serialize(key), min, max);
        if (isFutureConversion()) {
            addResultConverter(byteSetToStringSet);
        }
        return byteSetToStringSet.convert(results);
    }

    public Set<StringTuple> zRangeByScoreWithScores(String key, double min, double max, long offset, long count) {
        Set<Tuple> results = delegate.zRangeByScoreWithScores(serialize(key), min, max, offset, count);
        if (isFutureConversion()) {
            addResultConverter(tupleToStringTuple);
        }
        return tupleToStringTuple.convert(results);
    }

    public Set<StringTuple> zRangeByScoreWithScores(String key, double min, double max) {
        Set<Tuple> results = delegate.zRangeByScoreWithScores(serialize(key), min, max);
        if (isFutureConversion()) {
            addResultConverter(tupleToStringTuple);
        }
        return tupleToStringTuple.convert(results);
    }

    public Set<StringTuple> zRangeWithScores(String key, long start, long end) {
        Set<Tuple> results = delegate.zRangeWithScores(serialize(key), start, end);
        if (isFutureConversion()) {
            addResultConverter(tupleToStringTuple);
        }
        return tupleToStringTuple.convert(results);
    }

    public Long zRank(String key, String value) {
        Long result = delegate.zRank(serialize(key), serialize(value));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zRem(String key, String... values) {
        Long result = delegate.zRem(serialize(key), serializeMulti(values));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zRemRange(String key, long start, long end) {
        Long result = delegate.zRemRange(serialize(key), start, end);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zRemRangeByScore(String key, double min, double max) {
        Long result = delegate.zRemRangeByScore(serialize(key), min, max);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Set<String> zRevRange(String key, long start, long end) {
        Set<byte[]> results = delegate.zRevRange(serialize(key), start, end);
        if (isFutureConversion()) {
            addResultConverter(byteSetToStringSet);
        }
        return byteSetToStringSet.convert(results);
    }

    public Set<StringTuple> zRevRangeWithScores(String key, long start, long end) {
        Set<Tuple> results = delegate.zRevRangeWithScores(serialize(key), start, end);
        if (isFutureConversion()) {
            addResultConverter(tupleToStringTuple);
        }
        return tupleToStringTuple.convert(results);
    }

    public Set<String> zRevRangeByScore(String key, double min, double max) {
        Set<byte[]> results = delegate.zRevRangeByScore(serialize(key), min, max);
        if (isFutureConversion()) {
            addResultConverter(byteSetToStringSet);
        }
        return byteSetToStringSet.convert(results);
    }

    public Set<StringTuple> zRevRangeByScoreWithScores(String key, double min, double max) {
        Set<Tuple> results = delegate.zRevRangeByScoreWithScores(serialize(key), min, max);
        if (isFutureConversion()) {
            addResultConverter(tupleToStringTuple);
        }
        return tupleToStringTuple.convert(results);
    }

    public Set<String> zRevRangeByScore(String key, double min, double max, long offset, long count) {
        Set<byte[]> results = delegate.zRevRangeByScore(serialize(key), min, max, offset, count);
        if (isFutureConversion()) {
            addResultConverter(byteSetToStringSet);
        }
        return byteSetToStringSet.convert(results);
    }

    public Set<StringTuple> zRevRangeByScoreWithScores(String key, double min, double max, long offset, long count) {
        Set<Tuple> results = delegate.zRevRangeByScoreWithScores(serialize(key), min, max, offset, count);
        if (isFutureConversion()) {
            addResultConverter(tupleToStringTuple);
        }
        return tupleToStringTuple.convert(results);
    }

    public Long zRevRank(String key, String value) {
        Long result = delegate.zRevRank(serialize(key), serialize(value));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Double zScore(String key, String value) {
        Double result = delegate.zScore(serialize(key), serialize(value));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zUnionStore(String destKey, Aggregate aggregate, int[] weights, String... sets) {
        Long result = delegate.zUnionStore(serialize(destKey), aggregate, weights, serializeMulti(sets));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Long zUnionStore(String destKey, String... sets) {
        Long result = delegate.zUnionStore(serialize(destKey), serializeMulti(sets));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisGeoCommands#geoAdd(byte[], com.rocket.summer.framework.data.geo.Point, byte[])
     */
    @Override
    public Long geoAdd(byte[] key, Point point, byte[] member) {

        Long result = delegate.geoAdd(key, point, member);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisGeoCommands#geoAdd(byte[], com.rocket.summer.framework.data.redis.connection.RedisGeoCommands.GeoLocation)
     */
    public Long geoAdd(byte[] key, GeoLocation<byte[]> location) {

        Long result = delegate.geoAdd(key, location);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#geoAdd(java.lang.String, com.rocket.summer.framework.data.geo.Point, java.lang.String)
     */
    @Override
    public Long geoAdd(String key, Point point, String member) {
        return geoAdd(serialize(key), point, serialize(member));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#geoAdd(java.lang.String, com.rocket.summer.framework.data.redis.connection.RedisGeoCommands.GeoLocation)
     */
    @Override
    public Long geoAdd(String key, GeoLocation<String> location) {

        Assert.notNull(location, "Location must not be null!");
        return geoAdd(key, location.getPoint(), location.getName());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisGeoCommands#geoAdd(byte[], java.util.Map)
     */
    @Override
    public Long geoAdd(byte[] key, Map<byte[], Point> memberCoordinateMap) {

        Long result = delegate.geoAdd(key, memberCoordinateMap);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisGeoCommands#geoAdd(byte[], java.lang.Iterable)
     */
    @Override
    public Long geoAdd(byte[] key, Iterable<GeoLocation<byte[]>> locations) {

        Long result = delegate.geoAdd(key, locations);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#geoAdd(java.lang.String, java.util.Map)
     */
    @Override
    public Long geoAdd(String key, Map<String, Point> memberCoordinateMap) {

        Assert.notNull(memberCoordinateMap, "MemberCoordinateMap must not be null!");

        Map<byte[], Point> byteMap = new HashMap<byte[], Point>();
        for (Entry<String, Point> entry : memberCoordinateMap.entrySet()) {
            byteMap.put(serialize(entry.getKey()), entry.getValue());
        }

        return geoAdd(serialize(key), byteMap);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#geoAdd(java.lang.String, java.lang.Iterable)
     */
    @Override
    public Long geoAdd(String key, Iterable<GeoLocation<String>> locations) {

        Assert.notNull(locations, "Locations must not be null!");

        Map<byte[], Point> byteMap = new HashMap<byte[], Point>();
        for (GeoLocation<String> location : locations) {
            byteMap.put(serialize(location.getName()), location.getPoint());
        }

        return geoAdd(serialize(key), byteMap);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisGeoCommands#geoDist(byte[], byte[], byte[])
     */
    @Override
    public Distance geoDist(byte[] key, byte[] member1, byte[] member2) {

        Distance result = delegate.geoDist(key, member1, member2);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#geoDist(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Distance geoDist(String key, String member1, String member2) {
        return geoDist(serialize(key), serialize(member1), serialize(member2));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisGeoCommands#geoDist(byte[], byte[], byte[], com.rocket.summer.framework.data.geo.Metric)
     */
    @Override
    public Distance geoDist(byte[] key, byte[] member1, byte[] member2, Metric metric) {

        Distance result = delegate.geoDist(key, member1, member2, metric);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#geoDist(java.lang.String, java.lang.String, java.lang.String, com.rocket.summer.framework.data.redis.connection.RedisGeoCommands.DistanceUnit)
     */
    @Override
    public Distance geoDist(String key, String member1, String member2, Metric metric) {
        return geoDist(serialize(key), serialize(member1), serialize(member2), metric);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisGeoCommands#geoHash(byte[], byte[][])
     */
    @Override
    public List<String> geoHash(byte[] key, byte[]... members) {

        List<String> result = delegate.geoHash(key, members);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#geoHash(java.lang.String, java.lang.String[])
     */
    @Override
    public List<String> geoHash(String key, String... members) {

        List<String> result = delegate.geoHash(serialize(key), serializeMulti(members));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisGeoCommands#geoPos(byte[], byte[][])
     */
    @Override
    public List<Point> geoPos(byte[] key, byte[]... members) {

        List<Point> result = delegate.geoPos(key, members);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#geoPos(java.lang.String, java.lang.String[])
     */
    @Override
    public List<Point> geoPos(String key, String... members) {
        return geoPos(serialize(key), serializeMulti(members));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#geoRadius(java.lang.String, com.rocket.summer.framework.data.geo.Circle)
     */
    @Override
    public GeoResults<GeoLocation<String>> geoRadius(String key, Circle within) {

        GeoResults<GeoLocation<byte[]>> result = delegate.geoRadius(serialize(key), within);
        if (isFutureConversion()) {
            addResultConverter(byteGeoResultsToStringGeoResults);
        }

        return byteGeoResultsToStringGeoResults.convert(result);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#geoRadius(java.lang.String, com.rocket.summer.framework.data.geo.Circle, com.rocket.summer.framework.data.redis.core.GeoRadiusCommandArgs)
     */
    @Override
    public GeoResults<GeoLocation<String>> geoRadius(String key, Circle within, GeoRadiusCommandArgs args) {

        GeoResults<GeoLocation<byte[]>> result = delegate.geoRadius(serialize(key), within, args);
        if (isFutureConversion()) {
            addResultConverter(byteGeoResultsToStringGeoResults);
        }
        return byteGeoResultsToStringGeoResults.convert(result);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#geoRadiusByMember(java.lang.String, java.lang.String, double)
     */
    @Override
    public GeoResults<GeoLocation<String>> geoRadiusByMember(String key, String member, double radius) {
        return geoRadiusByMember(key, member, new Distance(radius, DistanceUnit.METERS));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#geoRadiusByMember(java.lang.String, java.lang.String, com.rocket.summer.framework.data.geo.Distance)
     */
    @Override
    public GeoResults<GeoLocation<String>> geoRadiusByMember(String key, String member, Distance radius) {

        GeoResults<GeoLocation<byte[]>> result = delegate.geoRadiusByMember(serialize(key), serialize(member), radius);
        if (isFutureConversion()) {
            addResultConverter(byteGeoResultsToStringGeoResults);
        }
        return byteGeoResultsToStringGeoResults.convert(result);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#geoRadiusByMember(java.lang.String, java.lang.String, com.rocket.summer.framework.data.geo.Distance, com.rocket.summer.framework.data.redis.core.GeoRadiusCommandArgs)
     */
    @Override
    public GeoResults<GeoLocation<String>> geoRadiusByMember(String key, String member, Distance radius,
                                                             GeoRadiusCommandArgs args) {

        GeoResults<GeoLocation<byte[]>> result = delegate.geoRadiusByMember(serialize(key), serialize(member), radius,
                args);
        if (isFutureConversion()) {
            addResultConverter(byteGeoResultsToStringGeoResults);
        }
        return byteGeoResultsToStringGeoResults.convert(result);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisGeoCommands#geoRadius(byte[], com.rocket.summer.framework.data.geo.Circle)
     */
    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadius(byte[] key, Circle within) {

        GeoResults<GeoLocation<byte[]>> result = delegate.geoRadius(key, within);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisGeoCommands#geoRadius(byte[], com.rocket.summer.framework.data.geo.Circle, com.rocket.summer.framework.data.redis.core.GeoRadiusCommandArgs)
     */
    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadius(byte[] key, Circle within, GeoRadiusCommandArgs args) {

        GeoResults<GeoLocation<byte[]>> result = delegate.geoRadius(key, within, args);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisGeoCommands#geoRadiusByMember(byte[], byte[], double)
     */
    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadiusByMember(byte[] key, byte[] member, double radius) {
        return geoRadiusByMember(key, member, new Distance(radius, DistanceUnit.METERS));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisGeoCommands#geoRadiusByMember(byte[], byte[], com.rocket.summer.framework.data.geo.Distance)
     */
    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadiusByMember(byte[] key, byte[] member, Distance radius) {

        GeoResults<GeoLocation<byte[]>> result = delegate.geoRadiusByMember(key, member, radius);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisGeoCommands#geoRadiusByMember(byte[], byte[], com.rocket.summer.framework.data.geo.Distance, com.rocket.summer.framework.data.redis.core.GeoRadiusCommandArgs)
     */
    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadiusByMember(byte[] key, byte[] member, Distance radius,
                                                             GeoRadiusCommandArgs args) {

        GeoResults<GeoLocation<byte[]>> result = delegate.geoRadiusByMember(key, member, radius, args);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisGeoCommands#geoRemove(byte[], byte[][])
     */
    @Override
    public Long geoRemove(byte[] key, byte[]... members) {
        return zRem(key, members);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#geoRemove(java.lang.String, java.lang.String[])
     */
    @Override
    public Long geoRemove(String key, String... members) {
        return geoRemove(serialize(key), serializeMulti(members));
    }

    public List<Object> closePipeline() {
        try {
            return convertResults(delegate.closePipeline(), pipelineConverters);
        } finally {
            pipelineConverters.clear();
        }
    }

    public boolean isPipelined() {
        return delegate.isPipelined();
    }

    public void openPipeline() {
        delegate.openPipeline();
    }

    public Object execute(String command) {
        return execute(command, (byte[][]) null);
    }

    public Object execute(String command, byte[]... args) {
        Object result = delegate.execute(command, args);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    public Object execute(String command, String... args) {
        return execute(command, serializeMulti(args));
    }

    public Boolean pExpire(String key, long millis) {
        return pExpire(serialize(key), millis);
    }

    public Boolean pExpireAt(String key, long unixTimeInMillis) {
        return pExpireAt(serialize(key), unixTimeInMillis);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#pTtl(java.lang.String)
     */
    @Override
    public Long pTtl(String key) {
        return pTtl(serialize(key));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#pTtl(java.lang.String, java.util.concurrent.TimeUnit)
     */
    @Override
    public Long pTtl(String key, TimeUnit timeUnit) {
        return pTtl(serialize(key), timeUnit);
    }

    public String scriptLoad(String script) {
        String result = delegate.scriptLoad(serialize(script));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /**
     * NOTE: This method will not deserialize Strings returned by Lua scripts, as they may not be encoded with the same
     * serializer used here. They will be returned as byte[]s
     */
    public <T> T eval(String script, ReturnType returnType, int numKeys, String... keysAndArgs) {
        T result = delegate.eval(serialize(script), returnType, numKeys, serializeMulti(keysAndArgs));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /**
     * NOTE: This method will not deserialize Strings returned by Lua scripts, as they may not be encoded with the same
     * serializer used here. They will be returned as byte[]s
     */
    public <T> T evalSha(String scriptSha1, ReturnType returnType, int numKeys, String... keysAndArgs) {
        T result = delegate.evalSha(scriptSha1, returnType, numKeys, serializeMulti(keysAndArgs));
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisServerCommands#time()
     */
    @Override
    public Long time() {
        return this.delegate.time();
    }

    @Override
    public List<RedisClientInfo> getClientList() {
        return this.delegate.getClientList();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisServerCommands#slaveOf(java.lang.String, int)
     */
    @Override
    public void slaveOf(String host, int port) {
        this.delegate.slaveOf(host, port);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisServerCommands#slaveOfNoOne()
     */
    @Override
    public void slaveOfNoOne() {
        this.delegate.slaveOfNoOne();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisKeyCommands#scan(com.rocket.summer.framework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<byte[]> scan(ScanOptions options) {
        return this.delegate.scan(options);
    }

    /*
     *
     */
    @Override
    public Cursor<Tuple> zScan(byte[] key, ScanOptions options) {
        return this.delegate.zScan(key, options);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisSetCommands#scan(byte[], com.rocket.summer.framework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<byte[]> sScan(byte[] key, ScanOptions options) {
        return this.delegate.sScan(key, options);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisHashCommands#hscan(byte[], com.rocket.summer.framework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<Entry<byte[], byte[]>> hScan(byte[] key, ScanOptions options) {
        return this.delegate.hScan(key, options);
    }

    /**
     * Specifies if pipelined and tx results should be deserialized to Strings. If false, results of
     * {@link #closePipeline()} and {@link #exec()} will be of the type returned by the underlying connection
     *
     * @param deserializePipelineAndTxResults Whether or not to deserialize pipeline and tx results
     */
    public void setDeserializePipelineAndTxResults(boolean deserializePipelineAndTxResults) {
        this.deserializePipelineAndTxResults = deserializePipelineAndTxResults;
    }

    private void addResultConverter(Converter<?, ?> converter) {
        if (isQueueing()) {
            txConverters.add(converter);
        } else {
            pipelineConverters.add(converter);
        }
    }

    private boolean isFutureConversion() {
        return isPipelined() || isQueueing();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<Object> convertResults(List<Object> results, Queue<Converter> converters) {
        if (!deserializePipelineAndTxResults || results == null) {
            return results;
        }
        if (results.size() != converters.size()) {
            // Some of the commands were done directly on the delegate, don't attempt to convert
            log.warn("Delegate returned an unexpected number of results. Abandoning type conversion.");
            return results;
        }
        List<Object> convertedResults = new ArrayList<Object>();
        for (Object result : results) {
            convertedResults.add(converters.remove().convert(result));
        }
        return convertedResults;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisServerCommands#setClientName(java.lang.String)
     */
    @Override
    public void setClientName(byte[] name) {
        this.delegate.setClientName(name);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#setClientName(java.lang.String)
     */
    @Override
    public void setClientName(String name) {
        setClientName(this.serializer.serialize(name));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisServerCommands#killClient(byte[])
     */
    @Override
    public void killClient(String host, int port) {
        this.delegate.killClient(host, port);
    }

    /*
     * @see com.rocket.summer.framework.data.redis.connection.RedisServerCommands#getClientName()
     */
    @Override
    public String getClientName() {
        return this.delegate.getClientName();
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#hScan(java.lang.String, com.rocket.summer.framework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<Entry<String, String>> hScan(String key, ScanOptions options) {

        return new ConvertingCursor<Map.Entry<byte[], byte[]>, Map.Entry<String, String>>(
                this.delegate.hScan(this.serialize(key), options),
                new Converter<Map.Entry<byte[], byte[]>, Map.Entry<String, String>>() {

                    @Override
                    public Entry<String, String> convert(final Entry<byte[], byte[]> source) {
                        return new Map.Entry<String, String>() {

                            @Override
                            public String getKey() {
                                return bytesToString.convert(source.getKey());
                            }

                            @Override
                            public String getValue() {
                                return bytesToString.convert(source.getValue());
                            }

                            @Override
                            public String setValue(String value) {
                                throw new UnsupportedOperationException("Cannot set value for entry in cursor");
                            }
                        };
                    }
                });
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#sScan(java.lang.String, com.rocket.summer.framework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<String> sScan(String key, ScanOptions options) {
        return new ConvertingCursor<byte[], String>(this.delegate.sScan(this.serialize(key), options), bytesToString);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#zScan(java.lang.String, com.rocket.summer.framework.data.redis.core.ScanOptions)
     */
    @Override
    public Cursor<StringTuple> zScan(String key, ScanOptions options) {
        return new ConvertingCursor<Tuple, StringRedisConnection.StringTuple>(delegate.zScan(this.serialize(key), options),
                new TupleConverter());
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        return delegate.getSentinelConnection();
    }

    @Override
    public Set<byte[]> zRangeByScore(String key, String min, String max) {
        Set<byte[]> results = delegate.zRangeByScore(serialize(key), min, max);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    @Override
    public Set<byte[]> zRangeByScore(String key, String min, String max, long offset, long count) {
        Set<byte[]> results = delegate.zRangeByScore(serialize(key), min, max, offset, count);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }
        return results;
    }

    @Override
    public Set<byte[]> zRangeByScore(byte[] key, String min, String max) {

        Set<byte[]> results = delegate.zRangeByScore(key, min, max);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }

        return results;
    }

    @Override
    public Set<byte[]> zRangeByScore(byte[] key, String min, String max, long offset, long count) {

        Set<byte[]> results = delegate.zRangeByScore(key, min, max, offset, count);
        if (isFutureConversion()) {
            addResultConverter(identityConverter);
        }

        return results;
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.HyperLogLogCommands#pfAdd(byte[], byte[][])
     */
    @Override
    public Long pfAdd(byte[] key, byte[]... values) {
        return delegate.pfAdd(key, values);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#pfAdd(java.lang.String, java.lang.String[])
     */
    @Override
    public Long pfAdd(String key, String... values) {
        return this.pfAdd(serialize(key), serializeMulti(values));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.HyperLogLogCommands#pfCount(byte[][])
     */
    @Override
    public Long pfCount(byte[]... keys) {
        return delegate.pfCount(keys);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#pfCount(java.lang.String[])
     */
    @Override
    public Long pfCount(String... keys) {
        return this.pfCount(serializeMulti(keys));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.HyperLogLogCommands#pfMerge(byte[], byte[][])
     */
    @Override
    public void pfMerge(byte[] destinationKey, byte[]... sourceKeys) {
        delegate.pfMerge(destinationKey, sourceKeys);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#pfMerge(java.lang.String, java.lang.String[][])
     */
    @Override
    public void pfMerge(String destinationKey, String... sourceKeys) {
        this.pfMerge(serialize(destinationKey), serializeMulti(sourceKeys));
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisZSetCommands#zRangeByLex(byte[])
     */
    @Override
    public Set<byte[]> zRangeByLex(byte[] key) {
        return delegate.zRangeByLex(key);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisZSetCommands#zRangeByLex(byte[], com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Range)
     */
    @Override
    public Set<byte[]> zRangeByLex(byte[] key, Range range) {
        return delegate.zRangeByLex(key, range);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisZSetCommands#zRangeByLex(byte[], com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Range, com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Limit)
     */
    @Override
    public Set<byte[]> zRangeByLex(byte[] key, Range range, Limit limit) {
        return delegate.zRangeByLex(key, range, limit);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#zRangeByLex(java.lang.String)
     */
    @Override
    public Set<String> zRangeByLex(String key) {
        return this.zRangeByLex(key, Range.unbounded());
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#zRangeByLex(java.lang.String, com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Range)
     */
    @Override
    public Set<String> zRangeByLex(String key, Range range) {
        return this.zRangeByLex(key, range, null);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.StringRedisConnection#zRangeByLex(java.lang.String, com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Range, com.rocket.summer.framework.data.redis.connection.RedisZSetCommands.Limit)
     */
    @Override
    public Set<String> zRangeByLex(String key, Range range, Limit limit) {

        Set<byte[]> results = delegate.zRangeByLex(serialize(key), range);
        if (isFutureConversion()) {
            addResultConverter(byteSetToStringSet);
        }
        return byteSetToStringSet.convert(results);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisServerCommands#migrate(byte[], com.rocket.summer.framework.data.redis.connection.RedisNode, int, com.rocket.summer.framework.data.redis.connection.RedisServerCommands.MigrateOption)
     */
    @Override
    public void migrate(byte[] key, RedisNode target, int dbIndex, MigrateOption option) {
        delegate.migrate(key, target, dbIndex, option);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.RedisServerCommands#migrate(byte[], com.rocket.summer.framework.data.redis.connection.RedisNode, int, com.rocket.summer.framework.data.redis.connection.RedisServerCommands.MigrateOption, long)
     */
    @Override
    public void migrate(byte[] key, RedisNode target, int dbIndex, MigrateOption option, long timeout) {
        delegate.migrate(key, target, dbIndex, option, timeout);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.redis.connection.DecoratedRedisConnection#getDelegate()
     */
    @Override
    public RedisConnection getDelegate() {
        return delegate;
    }

}

