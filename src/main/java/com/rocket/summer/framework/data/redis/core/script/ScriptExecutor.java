package com.rocket.summer.framework.data.redis.core.script;

import java.util.List;

import com.rocket.summer.framework.data.redis.serializer.RedisSerializer;

/**
 * Executes {@link RedisScript}s
 *
 * @author Jennifer Hickey
 * @param <K> The type of keys that may be passed during script execution
 */
public interface ScriptExecutor<K> {

    /**
     * Executes the given {@link RedisScript}
     *
     * @param script The script to execute
     * @param keys Any keys that need to be passed to the script
     * @param args Any args that need to be passed to the script
     * @return The return value of the script or null if {@link RedisScript#getResultType()} is null, likely indicating a
     *         throw-away status reply (i.e. "OK")
     */
    <T> T execute(RedisScript<T> script, List<K> keys, Object... args);

    /**
     * Executes the given {@link RedisScript}, using the provided {@link RedisSerializer}s to serialize the script
     * arguments and result.
     *
     * @param script The script to execute
     * @param argsSerializer The {@link RedisSerializer} to use for serializing args
     * @param resultSerializer The {@link RedisSerializer} to use for serializing the script return value
     * @param keys Any keys that need to be passed to the script
     * @param args Any args that need to be passed to the script
     * @return The return value of the script or null if {@link RedisScript#getResultType()} is null, likely indicating a
     *         throw-away status reply (i.e. "OK")
     */
    <T> T execute(RedisScript<T> script, RedisSerializer<?> argsSerializer, RedisSerializer<T> resultSerializer,
                  List<K> keys, Object... args);

}

