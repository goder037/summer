package com.rocket.summer.framework.data.redis.connection;

import java.util.List;

/**
 * Scripting commands.
 *
 * @author Costin Leau
 * @author Christoph Strobl
 * @author David Liu
 * @author Mark Paluch
 */
public interface RedisScriptingCommands {

    /**
     * Flush lua script cache.
     *
     * @see <a href="http://redis.io/commands/script-flush">Redis Documentation: SCRIPT FLUSH</a>
     */
    void scriptFlush();

    /**
     * Kill current lua script execution.
     *
     * @see <a href="http://redis.io/commands/script-kill">Redis Documentation: SCRIPT KILL</a>
     */
    void scriptKill();

    /**
     * Load lua script into scripts cache, without executing it.<br>
     * Execute the script by calling {@link #evalSha(byte[], ReturnType, int, byte[]...)}.
     *
     * @param script must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/script-load">Redis Documentation: SCRIPT LOAD</a>
     */
    String scriptLoad(byte[] script);

    /**
     * Check if given {@code scriptShas} exist in script cache.
     *
     * @param scriptShas
     * @return one entry per given scriptSha in returned list.
     * @see <a href="http://redis.io/commands/script-exists">Redis Documentation: SCRIPT EXISTS</a>
     */
    List<Boolean> scriptExists(String... scriptShas);

    /**
     * Evaluate given {@code script}.
     *
     * @param script must not be {@literal null}.
     * @param returnType must not be {@literal null}.
     * @param numKeys
     * @param keysAndArgs must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/eval">Redis Documentation: EVAL</a>
     */
    <T> T eval(byte[] script, ReturnType returnType, int numKeys, byte[]... keysAndArgs);

    /**
     * Evaluate given {@code scriptSha}.
     *
     * @param scriptSha must not be {@literal null}.
     * @param returnType must not be {@literal null}.
     * @param numKeys
     * @param keysAndArgs must not be {@literal null}.
     * @return
     * @see <a href="http://redis.io/commands/evalsha">Redis Documentation: EVALSHA</a>
     */
    <T> T evalSha(String scriptSha, ReturnType returnType, int numKeys, byte[]... keysAndArgs);

    /**
     * Evaluate given {@code scriptSha}.
     *
     * @param scriptSha must not be {@literal null}.
     * @param returnType must not be {@literal null}.
     * @param numKeys
     * @param keysAndArgs must not be {@literal null}.
     * @return
     * @since 1.5
     * @see <a href="http://redis.io/commands/evalsha">Redis Documentation: EVALSHA</a>
     */
    <T> T evalSha(byte[] scriptSha, ReturnType returnType, int numKeys, byte[]... keysAndArgs);
}

