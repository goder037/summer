package com.rocket.summer.framework.data.redis.core.script;

/**
 * A script to be executed using the <a href="http://redis.io/commands/eval">Redis scripting support</a> available as of
 * version 2.6
 *
 * @author Jennifer Hickey
 * @param <T> The script result type. Should be one of Long, Boolean, List, or deserialized value type. Can be null if
 *          the script returns a throw-away status (i.e "OK")
 */
public interface RedisScript<T> {

    /**
     * @return The SHA1 of the script, used for executing Redis evalsha command
     */
    String getSha1();

    /**
     * @return The script result type. Should be one of Long, Boolean, List, or deserialized value type. Can be null if
     *         the script returns a throw-away status (i.e "OK")
     */
    Class<T> getResultType();

    /**
     * @return The script contents
     */
    String getScriptAsString();

}

