package com.rocket.summer.framework.data.redis.connection;

/**
 * Interface for the commands supported by Redis.
 *
 * @author Costin Leau
 * @author Christoph Strobl
 */
public interface RedisCommands extends RedisKeyCommands, RedisStringCommands, RedisListCommands, RedisSetCommands,
        RedisZSetCommands, RedisHashCommands, RedisTxCommands, RedisPubSubCommands, RedisConnectionCommands,
        RedisServerCommands, RedisScriptingCommands, RedisGeoCommands, HyperLogLogCommands {

    /**
     * 'Native' or 'raw' execution of the given command along-side the given arguments. The command is executed as is,
     * with as little 'interpretation' as possible - it is up to the caller to take care of any processing of arguments or
     * the result.
     *
     * @param command Command to execute
     * @param args Possible command arguments (may be null)
     * @return execution result.
     */
    Object execute(String command, byte[]... args);
}

