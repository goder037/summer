package com.rocket.summer.framework.data.redis.core.script;

import java.util.ArrayList;
import java.util.List;

import com.rocket.summer.framework.dao.DataAccessException;
import com.rocket.summer.framework.dao.NonTransientDataAccessException;
import com.rocket.summer.framework.data.redis.RedisSystemException;
import com.rocket.summer.framework.data.redis.connection.RedisConnection;
import com.rocket.summer.framework.data.redis.connection.ReturnType;
import com.rocket.summer.framework.data.redis.core.RedisCallback;
import com.rocket.summer.framework.data.redis.core.RedisTemplate;
import com.rocket.summer.framework.data.redis.serializer.RedisSerializer;

/**
 * Default implementation of {@link ScriptExecutor}. Optimizes performance by attempting to execute script first using
 * evalsha, then falling back to eval if Redis has not yet cached the script. Evalsha is not attempted if the script is
 * executed in a pipeline or transaction.
 *
 * @author Jennifer Hickey
 * @author Christoph Strobl
 * @author Thomas Darimont
 * @param <K> The type of keys that may be passed during script execution
 */
public class DefaultScriptExecutor<K> implements ScriptExecutor<K> {

    private RedisTemplate<K, ?> template;

    /**
     * @param template The {@link RedisTemplate} to use
     */
    public DefaultScriptExecutor(RedisTemplate<K, ?> template) {
        this.template = template;
    }

    @SuppressWarnings("unchecked")
    public <T> T execute(final RedisScript<T> script, final List<K> keys, final Object... args) {
        // use the Template's value serializer for args and result
        return execute(script, template.getValueSerializer(), (RedisSerializer<T>) template.getValueSerializer(), keys,
                args);
    }

    public <T> T execute(final RedisScript<T> script, final RedisSerializer<?> argsSerializer,
                         final RedisSerializer<T> resultSerializer, final List<K> keys, final Object... args) {
        return template.execute(new RedisCallback<T>() {
            public T doInRedis(RedisConnection connection) throws DataAccessException {
                final ReturnType returnType = ReturnType.fromJavaType(script.getResultType());
                final byte[][] keysAndArgs = keysAndArgs(argsSerializer, keys, args);
                final int keySize = keys != null ? keys.size() : 0;
                if (connection.isPipelined() || connection.isQueueing()) {
                    // We could script load first and then do evalsha to ensure sha is present,
                    // but this adds a sha1 to exec/closePipeline results. Instead, just eval
                    connection.eval(scriptBytes(script), returnType, keySize, keysAndArgs);
                    return null;
                }
                return eval(connection, script, returnType, keySize, keysAndArgs, resultSerializer);
            }
        });
    }

    protected <T> T eval(RedisConnection connection, RedisScript<T> script, ReturnType returnType, int numKeys,
                         byte[][] keysAndArgs, RedisSerializer<T> resultSerializer) {

        Object result;
        try {
            result = connection.evalSha(script.getSha1(), returnType, numKeys, keysAndArgs);
        } catch (Exception e) {

            if (!exceptionContainsNoScriptError(e)) {
                throw e instanceof RuntimeException ? (RuntimeException) e : new RedisSystemException(e.getMessage(), e);
            }

            result = connection.eval(scriptBytes(script), returnType, numKeys, keysAndArgs);
        }

        if (script.getResultType() == null) {
            return null;
        }

        return deserializeResult(resultSerializer, result);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected byte[][] keysAndArgs(RedisSerializer argsSerializer, List<K> keys, Object[] args) {
        final int keySize = keys != null ? keys.size() : 0;
        byte[][] keysAndArgs = new byte[args.length + keySize][];
        int i = 0;
        if (keys != null) {
            for (K key : keys) {
                if (keySerializer() == null && key instanceof byte[]) {
                    keysAndArgs[i++] = (byte[]) key;
                } else {
                    keysAndArgs[i++] = keySerializer().serialize(key);
                }
            }
        }
        for (Object arg : args) {
            if (argsSerializer == null && arg instanceof byte[]) {
                keysAndArgs[i++] = (byte[]) arg;
            } else {
                keysAndArgs[i++] = argsSerializer.serialize(arg);
            }
        }
        return keysAndArgs;
    }

    protected byte[] scriptBytes(RedisScript<?> script) {
        return template.getStringSerializer().serialize(script.getScriptAsString());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected <T> T deserializeResult(RedisSerializer<T> resultSerializer, Object result) {
        if (result instanceof byte[]) {
            if (resultSerializer == null) {
                return (T) result;
            }
            return resultSerializer.deserialize((byte[]) result);
        }
        if (result instanceof List) {
            List results = new ArrayList();
            for (Object obj : (List) result) {
                results.add(deserializeResult(resultSerializer, obj));
            }
            return (T) results;
        }
        return (T) result;
    }

    @SuppressWarnings("rawtypes")
    protected RedisSerializer keySerializer() {
        return template.getKeySerializer();
    }

    private boolean exceptionContainsNoScriptError(Exception e) {

        if (!(e instanceof NonTransientDataAccessException)) {
            return false;
        }

        Throwable current = e;
        while (current != null) {

            String exMessage = current.getMessage();
            if (exMessage != null && exMessage.contains("NOSCRIPT")) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }

}

