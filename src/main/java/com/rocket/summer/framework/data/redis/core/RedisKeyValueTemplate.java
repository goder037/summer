package com.rocket.summer.framework.data.redis.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rocket.summer.framework.data.keyvalue.core.KeyValueAdapter;
import com.rocket.summer.framework.data.keyvalue.core.KeyValueCallback;
import com.rocket.summer.framework.data.keyvalue.core.KeyValueTemplate;
import com.rocket.summer.framework.data.redis.core.mapping.RedisMappingContext;
import com.rocket.summer.framework.util.Assert;
import com.rocket.summer.framework.util.ClassUtils;

/**
 * Redis specific implementation of {@link KeyValueTemplate}.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public class RedisKeyValueTemplate extends KeyValueTemplate {

    /**
     * Create new {@link RedisKeyValueTemplate}.
     *
     * @param adapter must not be {@literal null}.
     * @param mappingContext must not be {@literal null}.
     */
    public RedisKeyValueTemplate(RedisKeyValueAdapter adapter, RedisMappingContext mappingContext) {
        super(adapter, mappingContext);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.KeyValueTemplate#getMappingContext()
     */
    @Override
    public RedisMappingContext getMappingContext() {
        return (RedisMappingContext) super.getMappingContext();
    }

    /**
     * Retrieve entities by resolving their {@literal id}s and converting them into required type. <br />
     * The callback provides either a single {@literal id} or an {@link Iterable} of {@literal id}s, used for retrieving
     * the actual domain types and shortcuts manual retrieval and conversion of {@literal id}s via {@link RedisTemplate}.
     *
     * <pre>
     * <code>
     * List&#60;RedisSession&#62; sessions = template.find(new RedisCallback&#60;Set&#60;byte[]&#62;&#62;() {
     *   public Set&#60;byte[]&#60; doInRedis(RedisConnection connection) throws DataAccessException {
     *     return connection
     *       .sMembers("spring:session:sessions:securityContext.authentication.principal.username:user"
     *         .getBytes());
     *   }
     * }, RedisSession.class);
     * </code>
     *
     * <pre>
     *
     * @param callback provides the to retrieve entity ids. Must not be {@literal null}.
     * @param type must not be {@literal null}.
     * @return empty list if not elements found.
     */
    public <T> List<T> find(final RedisCallback<?> callback, final Class<T> type) {

        Assert.notNull(callback, "Callback must not be null.");

        return execute(new RedisKeyValueCallback<List<T>>() {

            @Override
            public List<T> doInRedis(RedisKeyValueAdapter adapter) {

                Object callbackResult = adapter.execute(callback);

                if (callbackResult == null) {
                    return Collections.emptyList();
                }

                Iterable<?> ids = ClassUtils.isAssignable(Iterable.class, callbackResult.getClass())
                        ? (Iterable<?>) callbackResult : Collections.singleton(callbackResult);

                List<T> result = new ArrayList<T>();
                for (Object id : ids) {

                    String idToUse = adapter.getConverter().getConversionService().canConvert(id.getClass(), String.class)
                            ? adapter.getConverter().getConversionService().convert(id, String.class) : id.toString();

                    T candidate = findById(idToUse, type);
                    if (candidate != null) {
                        result.add(candidate);
                    }
                }

                return result;
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.KeyValueTemplate#insert(java.io.Serializable, java.lang.Object)
     */
    @Override
    public void insert(final Serializable id, final Object objectToInsert) {

        if (objectToInsert instanceof PartialUpdate) {
            doPartialUpdate((PartialUpdate<?>) objectToInsert);
            return;
        }

        super.insert(id, objectToInsert);
    }

    /*
     * (non-Javadoc)
     * @see com.rocket.summer.framework.data.keyvalue.core.KeyValueTemplate#update(java.lang.Object)
     */
    @Override
    public void update(Object objectToUpdate) {

        if (objectToUpdate instanceof PartialUpdate) {
            doPartialUpdate((PartialUpdate<?>) objectToUpdate);
            return;
        }

        super.update(objectToUpdate);
    }

    protected void doPartialUpdate(final PartialUpdate<?> update) {

        execute(new RedisKeyValueCallback<Void>() {

            @Override
            public Void doInRedis(RedisKeyValueAdapter adapter) {

                adapter.update(update);
                return null;
            }
        });
    }

    /**
     * Redis specific {@link KeyValueCallback}.
     *
     * @author Christoph Strobl
     * @param <T>
     * @since 1.7
     */
    public static abstract class RedisKeyValueCallback<T> implements KeyValueCallback<T> {

        /*
         * (non-Javadoc)
         * @see com.rocket.summer.framework.data.keyvalue.core.KeyValueCallback#doInKeyValue(com.rocket.summer.framework.data.keyvalue.core.KeyValueAdapter)
         */
        @Override
        public T doInKeyValue(KeyValueAdapter adapter) {
            return doInRedis((RedisKeyValueAdapter) adapter);
        }

        public abstract T doInRedis(RedisKeyValueAdapter adapter);
    }

}

