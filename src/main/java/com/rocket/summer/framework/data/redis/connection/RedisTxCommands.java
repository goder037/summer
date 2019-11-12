package com.rocket.summer.framework.data.redis.connection;

import java.util.List;

/**
 * Transaction/Batch specific commands supported by Redis.
 *
 * @author Costin Leau
 * @author Christoph Strobl
 * @author Mark Paluch
 */
public interface RedisTxCommands {

    /**
     * Mark the start of a transaction block. <br>
     * Commands will be queued and can then be executed by calling {@link #exec()} or rolled back using {@link #discard()}
     * <p>
     *
     * @see <a href="http://redis.io/commands/multi">Redis Documentation: MULTI</a>
     */
    void multi();

    /**
     * Executes all queued commands in a transaction started with {@link #multi()}. <br>
     * If used along with {@link #watch(byte[]...)} the operation will fail if any of watched keys has been modified.
     *
     * @return List of replies for each executed command.
     * @see <a href="http://redis.io/commands/exec">Redis Documentation: EXEC</a>
     */
    List<Object> exec();

    /**
     * Discard all commands issued after {@link #multi()}.
     *
     * @see <a href="http://redis.io/commands/discard">Redis Documentation: DISCARD</a>
     */
    void discard();

    /**
     * Watch given {@code keys} for modifications during transaction started with {@link #multi()}.
     *
     * @param keys must not be {@literal null}.
     * @see <a href="http://redis.io/commands/watch">Redis Documentation: WATCH</a>
     */
    void watch(byte[]... keys);

    /**
     * Flushes all the previously {@link #watch(byte[]...)} keys.
     *
     * @see <a href="http://redis.io/commands/unwatch">Redis Documentation: UNWATCH</a>
     */
    void unwatch();
}
